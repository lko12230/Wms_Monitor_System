package com.wms.monitor.entity;

import java.util.Date;

public class JobExecutionLog {

    private String jobName;
    private String status;
    private Date startTime;
    private Date endTime;
    private long durationSec;
    private String errorMessage;
    private String executedBy;

    // Getters & Setters
    public String getJobName() { return jobName; }
    public void setJobName(String jobName) { this.jobName = jobName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }

    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }

    public long getDurationSec() { return durationSec; }
    public void setDurationSec(long durationSec) { this.durationSec = durationSec; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getExecutedBy() { return executedBy; }
    public void setExecutedBy(String executedBy) { this.executedBy = executedBy; }
}