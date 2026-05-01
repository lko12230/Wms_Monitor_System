package com.wms.monitor.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

@Repository
public class MailConfigRepository {

    @Value("${maildb.url}")
    private String url;

    @Value("${maildb.username}")
    private String username;

    @Value("${maildb.password}")
    private String password;

    public Map<String, String> getAllConfigs() {

        Map<String, String> map = new HashMap<>();

        String sql = "SELECT config_key, config_value FROM mail_config WHERE active='Y'";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                map.put(rs.getString("config_key"), rs.getString("config_value"));
            }

        } catch (Exception e) {
            System.out.println("❌ Failed to fetch mail config");
            e.printStackTrace();
        }

        return map;
    }
}