package com.wms.monitor.service;

import com.wms.monitor.entity.WarehouseConfig;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;

@Service
public class DynamicConnectionService {

    public Connection getConnection(WarehouseConfig config) throws Exception {
        return DriverManager.getConnection(
                config.getUrl(),
                config.getUsername(),
                config.getPassword()
        );
    }
}