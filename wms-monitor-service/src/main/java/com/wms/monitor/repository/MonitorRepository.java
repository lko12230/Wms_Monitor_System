package com.wms.monitor.repository;

import com.wms.monitor.entity.MonitorLog;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MonitorRepository {

    // 🔹 Run Procedure
    public void runMonitorProcedure(Connection conn) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(
                "BEGIN wms_monitor.run_daily; END;")) {
            ps.execute();
        }
    }

    // 🔹 Fetch Logs (ORDER BY LOG_ID ASC ✅)
    public List<MonitorLog> getLogs(Connection conn) throws Exception {

        List<MonitorLog> list = new ArrayList<>();

        String sql = """
            SELECT log_id, check_code, check_desc, row_count, updated_rows, addwho, adddate
            FROM wms_monitor_log
            WHERE TRUNC(adddate) = TRUNC(SYSDATE)
            ORDER BY log_id ASC
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MonitorLog log = new MonitorLog();

                // 🔥 IMPORTANT: map log_id also
                log.setLogId(rs.getLong("log_id"));
                log.setCheckCode(rs.getString("check_code"));
                log.setCheckDesc(rs.getString("check_desc"));
                log.setRowCount(rs.getInt("row_count"));
                log.setUpdatedRows(rs.getInt("updated_rows"));
                log.setAddWho(rs.getString("addwho"));
                log.setAddDate(rs.getTimestamp("adddate"));

                list.add(log);
            }
        }

        return list;
    }
}