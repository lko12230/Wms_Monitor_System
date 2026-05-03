package com.wms.monitor.service;

import java.io.File;

public class MonitorResult {

    private File file;
    private boolean hasMismatch;
    private String mismatchWhList;
    private String dateTime; // ✅ NEW FIELD

    // ✅ UPDATED CONSTRUCTOR
    public MonitorResult(File file, boolean hasMismatch, String mismatchWhList, String dateTime) {
        this.file = file;
        this.hasMismatch = hasMismatch;
        this.mismatchWhList = mismatchWhList;
        this.dateTime = dateTime;
    }

    // ✅ OPTIONAL (Backward compatibility if needed)
    public MonitorResult(File file, boolean hasMismatch, String mismatchWhList) {
        this.file = file;
        this.hasMismatch = hasMismatch;
        this.mismatchWhList = mismatchWhList;
        this.dateTime = ""; // or null
    }

    public File getFile() {
        return file;
    }

    public boolean isHasMismatch() {
        return hasMismatch;
    }

    public String getMismatchWhList() {
        return mismatchWhList;
    }

    public String getDateTime() {
        return dateTime;
    }
}