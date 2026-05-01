package com.wms.monitor.service;

import java.io.File;

public class MonitorResult {

    private File file;
    private boolean hasMismatch;
    private String mismatchWhList;

    public MonitorResult(File file, boolean hasMismatch, String mismatchWhList) {
        this.file = file;
        this.hasMismatch = hasMismatch;
        this.mismatchWhList = mismatchWhList;
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
}