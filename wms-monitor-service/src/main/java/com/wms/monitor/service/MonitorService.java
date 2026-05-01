package com.wms.monitor.service;

import com.wms.monitor.entity.MonitorLog;
import com.wms.monitor.entity.WarehouseConfig;
import com.wms.monitor.repository.MonitorRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.List;

@Service
public class MonitorService {

    @Autowired
    private WarehouseConfigService warehouseService;

    @Autowired
    private DynamicConnectionService connectionService;

    @Autowired
    private MonitorRepository repository;

    public MonitorResult generateExcelReport() throws Exception {

        Workbook workbook = new XSSFWorkbook();

        // ================= STYLE (RED) =================
        CellStyle redStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setColor(IndexedColors.RED.getIndex());
        redStyle.setFont(font);

        // ================= SUMMARY =================
        Sheet summary = workbook.createSheet("SUMMARY");
        Row header = summary.createRow(0);
        header.createCell(0).setCellValue("Warehouse");
        header.createCell(1).setCellValue("Issues");

        int summaryRow = 1;

        // ================= TRACKING =================
        boolean hasMismatch = false;
        StringBuilder mismatchWh = new StringBuilder();

        // ================= LOOP =================
        for (WarehouseConfig config : warehouseService.getAllWarehouses()) {

            boolean warehouseMismatch = false; // 🔥 per warehouse flag

            try (Connection conn = connectionService.getConnection(config)) {

                repository.runMonitorProcedure(conn);
                List<MonitorLog> logs = repository.getLogs(conn);

                Sheet sheet = workbook.createSheet(config.getName());

                Row h = sheet.createRow(0);
                h.createCell(0).setCellValue("Check Code");
                h.createCell(1).setCellValue("Description");
                h.createCell(2).setCellValue("Row Count");
                h.createCell(3).setCellValue("Updated Rows");

                int rowNum = 1;

                if (logs == null || logs.isEmpty()) {
                    Row row = sheet.createRow(rowNum);
                    row.createCell(0).setCellValue("NO DATA");
                } else {

                    for (MonitorLog log : logs) {

                        Row row = sheet.createRow(rowNum++);

                        row.createCell(0).setCellValue(log.getCheckCode());
                        row.createCell(1).setCellValue(log.getCheckDesc());
                        row.createCell(2).setCellValue(log.getRowCount());
                        row.createCell(3).setCellValue(log.getUpdatedRows());

                        // 🔴 MISMATCH DETECTION
                        if (log.getRowCount() > 0) {
                            row.getCell(2).setCellStyle(redStyle);

                            hasMismatch = true;
                            warehouseMismatch = true;
                        }
                    }
                }

                // ================= ADD TO SUMMARY =================
                Row sRow = summary.createRow(summaryRow++);
                sRow.createCell(0).setCellValue(config.getName());
                sRow.createCell(1).setCellValue(logs != null ? logs.size() : 0);

                // 🔥 ADD WAREHOUSE NAME IF MISMATCH FOUND
                if (warehouseMismatch) {
                    if (mismatchWh.length() > 0) {
                        mismatchWh.append(", ");
                    }
                    mismatchWh.append(config.getName());
                }

            } catch (Exception e) {
                System.out.println("❌ Failed for " + config.getName());
                e.printStackTrace();
            }
        }

        // ================= SAVE FILE =================
        File file = new File("WMS_Monitor_All_WH.xlsx");
        FileOutputStream out = new FileOutputStream(file);
        workbook.write(out);
        workbook.close();

        // ================= RETURN RESULT =================
        return new MonitorResult(
                file,
                hasMismatch,
                mismatchWh.toString()
        );
    }
}