package com.wms.monitor.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Repository
public class SchedulerConfigRepository {

    @Value("${maildb.url}")
    private String url;

    @Value("${maildb.username}")
    private String username;

    @Value("${maildb.password}")
    private String password;

    private static final String DEFAULT_CRON = "0 0 8 * * ?";

    public String getCron() {

        String sql = "SELECT cron_expression FROM job_config WHERE job_name='WMS_MONITOR'";
        String cron = DEFAULT_CRON;

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String dbCron = rs.getString("cron_expression");
                if (dbCron != null && !dbCron.trim().isEmpty()) {
                    cron = dbCron;
                }
            }

        } catch (Exception e) {
            System.out.println("⚠️ Using default cron");
        }

        return cron;
    }
}