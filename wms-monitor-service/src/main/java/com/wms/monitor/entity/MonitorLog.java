package com.wms.monitor.entity;

import java.util.Date;

public class MonitorLog {

    private String checkCode;
    private String checkDesc;
    private int rowCount;
    private int updatedRows;
    private String addWho;
    private Date addDate;      // 🔥 IMPORTANT (timestamp)
    private String warehouse;  // 🔥 for multi-WH

    // ================= GETTERS & SETTERS =================

    public String getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }

    public String getCheckDesc() {
        return checkDesc;
    }

    public void setCheckDesc(String checkDesc) {
        this.checkDesc = checkDesc;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public int getUpdatedRows() {
        return updatedRows;
    }

    public void setUpdatedRows(int updatedRows) {
        this.updatedRows = updatedRows;
    }

    public String getAddWho() {
        return addWho;
    }

    public void setAddWho(String addWho) {
        this.addWho = addWho;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    // ================= OPTIONAL (DEBUG FRIENDLY) =================

    @Override
    public String toString() {
        return "MonitorLog{" +
                "checkCode='" + checkCode + '\'' +
                ", checkDesc='" + checkDesc + '\'' +
                ", rowCount=" + rowCount +
                ", updatedRows=" + updatedRows +
                ", addWho='" + addWho + '\'' +
                ", addDate=" + addDate +
                ", warehouse='" + warehouse + '\'' +
                '}';
    }
}