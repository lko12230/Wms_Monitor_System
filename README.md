# 🚀 WMS Monitor System

A **Spring Boot-based Warehouse Monitoring System** that automates warehouse health checks, detects mismatches, generates Excel reports, and sends email alerts.

---

## 📌 Overview

WMS Monitor System is designed to monitor multiple warehouse databases, identify inconsistencies, and notify stakeholders automatically.

It eliminates manual monitoring and ensures **real-time visibility of data issues across warehouses**.

---

## ✨ Key Features

* 🔍 **Automated Monitoring**

  * Executes database validation procedures for each warehouse

* 📊 **Excel Report Generation**

  * Generates detailed reports for each warehouse
  * Highlights mismatches (Row Count > 0) in **RED**

* 🚨 **Mismatch Detection**

  * Detects inconsistencies across warehouses
  * Adds **remarks like**:

    * `Mismatch found in WH1, WH2`

* 📧 **Email Notification System**

  * Sends automated emails with:

    * Professional HTML template
    * Inline company logo
    * Excel report attachment

* ⏰ **Dynamic Scheduler**

  * Cron-based execution (default: **8 AM daily**)
  * Configurable via database

* 🏭 **Multi-Warehouse Support**

  * Supports multiple warehouse connections dynamically

* 📝 **Job Execution Logging**

  * Tracks:

    * Status (SUCCESS / FAILED)
    * Execution time
    * Error messages

---

## 🛠️ Tech Stack

* **Backend:** Java 17, Spring Boot 3
* **Database:** Oracle
* **Email:** JavaMailSender (SMTP)
* **Excel:** Apache POI
* **Scheduler:** Spring Scheduling (Dynamic Cron)
* **Build Tool:** Maven

---

## 📂 Project Structure

```
com.wms.monitor
│
├── config / scheduler
│   └── DynamicScheduler.java
│
├── service
│   ├── MonitorService.java
│   ├── DynamicEmailService.java
│   └── DynamicConnectionService.java
│
├── repository
│   ├── MonitorRepository.java
│   ├── EmailConfigRepository.java
│   └── SchedulerConfigRepository.java
│
├── entity
│   ├── MonitorLog.java
│   ├── WarehouseConfig.java
│   └── JobExecutionLog.java
│
└── resources
    ├── application.properties
    └── static/logo.jpg
```

---

## ⚙️ How It Works

1. Scheduler triggers job based on cron expression
2. System connects to each warehouse database
3. Executes monitoring stored procedure
4. Fetches results (logs)
5. Generates Excel report:

   * Red → mismatches found
   * Green → no issues
6. Prepares email content with remarks
7. Sends email with attachment

---

## 📧 Email Output

* Professional HTML email
* Company logo embedded
* Status:

  * ✅ Completed (No Issues)
  * ❌ Mismatch Found
* Attached Excel Report

---

## 📊 Excel Report

Each warehouse sheet contains:

| Column       | Description    |
| ------------ | -------------- |
| Check Code   | Validation ID  |
| Description  | Check details  |
| Row Count    | Mismatch count |
| Updated Rows | Affected rows  |

* 🔴 Row Count > 0 → Highlighted in RED
* 🟢 All 0 → No mismatch

---

## 🧾 Job Execution Log Table

Tracks job execution:

| Column        | Description      |
| ------------- | ---------------- |
| JOB_NAME      | Job identifier   |
| STATUS        | SUCCESS / FAILED |
| START_TIME    | Start time       |
| END_TIME      | End time         |
| DURATION_SEC  | Execution time   |
| ERROR_MESSAGE | Error details    |

---

## 🕒 Default Cron

```
0 0 8 * * ?
```

➡ Runs **every morning at 8 AM**

---

## 🚀 How to Run

```bash
mvn clean install
java -jar target/wms-monitor-service-0.0.1-SNAPSHOT.jar
```

---

## 🔐 Configuration

Update in database:

* Mail Config (SMTP settings)
* Scheduler Config (cron expression)
* Warehouse Config (DB connections)

---

## 📈 Future Enhancements

* Dashboard UI
* Real-time alerts (Slack/Teams)
* Retry mechanism improvements
* Performance optimization for large datasets

---

## 👨‍💻 Author

**Ayush Gupta**
Software Engineer | Backend Developer

---

## ⭐ If you like this project

Give it a ⭐ on GitHub and share!

---
