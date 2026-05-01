package com.wms.monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(
        exclude = { DataSourceAutoConfiguration.class } // 🔥 disable DB auto check
)
@EnableScheduling // 🔥 enable scheduler
public class WmsMonitorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WmsMonitorServiceApplication.class, args);
        System.out.println("🚀 WMS Monitor Service Started Successfully...");
    }
}