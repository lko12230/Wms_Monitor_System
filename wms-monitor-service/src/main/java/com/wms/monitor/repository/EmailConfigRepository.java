package com.wms.monitor.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EmailConfigRepository {

    @Value("${maildb.url}")
    private String url;

    @Value("${maildb.username}")
    private String username;

    @Value("${maildb.password}")
    private String password;

    public List<String> getEmails() {

        List<String> emails = new ArrayList<>();

        String sql = "SELECT email FROM email_config WHERE active='Y'";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                emails.add(rs.getString("email"));
            }

        } catch (Exception e) {
            System.out.println("❌ Failed to fetch emails");
            e.printStackTrace();
        }

        return emails;
    }
}