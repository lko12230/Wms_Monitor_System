package com.wms.monitor.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

@Repository
public class JobExecutionRepository {

    @Value("${maildb.url}")
    private String url;

    @Value("${maildb.username}")
    private String username;

    @Value("${maildb.password}")
    private String password;

    public void saveLog(String status, String error, long duration) {

        String sql = """
            INSERT INTO job_execution_log 
            (job_name, status, start_time, end_time, duration_sec, error_message, executed_by)
            VALUES ('WMS_MONITOR', ?, SYSDATE - (?/86400), SYSDATE, ?, ?, USER)
        """;

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setLong(2, duration);
            ps.setLong(3, duration);
            ps.setString(4, error);

            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("❌ Failed to save job log");
            e.printStackTrace();
        }
    }
}