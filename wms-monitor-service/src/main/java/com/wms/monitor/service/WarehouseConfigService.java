package com.wms.monitor.service;

import com.wms.monitor.entity.WarehouseConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WarehouseConfigService {

    // ===== WH1 =====
    @Value("${wh1.name}") private String wh1Name;
    @Value("${wh1.url}") private String wh1Url;
    @Value("${wh1.username}") private String wh1User;
    @Value("${wh1.password}") private String wh1Pass;

    // ===== WH2 =====
    @Value("${wh2.name}") private String wh2Name;
    @Value("${wh2.url}") private String wh2Url;
    @Value("${wh2.username}") private String wh2User;
    @Value("${wh2.password}") private String wh2Pass;

    // ===== WH3 =====
    @Value("${wh3.name}") private String wh3Name;
    @Value("${wh3.url}") private String wh3Url;
    @Value("${wh3.username}") private String wh3User;
    @Value("${wh3.password}") private String wh3Pass;

    // ===== WH4 =====
    @Value("${wh4.name}") private String wh4Name;
    @Value("${wh4.url}") private String wh4Url;
    @Value("${wh4.username}") private String wh4User;
    @Value("${wh4.password}") private String wh4Pass;

    // ===== WH5 =====
    @Value("${wh5.name}") private String wh5Name;
    @Value("${wh5.url}") private String wh5Url;
    @Value("${wh5.username}") private String wh5User;
    @Value("${wh5.password}") private String wh5Pass;

    // ===== WH6 =====
    @Value("${wh6.name}") private String wh6Name;
    @Value("${wh6.url}") private String wh6Url;
    @Value("${wh6.username}") private String wh6User;
    @Value("${wh6.password}") private String wh6Pass;

    // 🔥 FINAL METHOD
    public List<WarehouseConfig> getAllWarehouses() {

        List<WarehouseConfig> list = new ArrayList<>();

        list.add(new WarehouseConfig(wh1Name, wh1Url, wh1User, wh1Pass));
        list.add(new WarehouseConfig(wh3Name, wh3Url, wh3User, wh3Pass));
        list.add(new WarehouseConfig(wh4Name, wh4Url, wh4User, wh4Pass));
        list.add(new WarehouseConfig(wh5Name, wh5Url, wh5User, wh5Pass));
        list.add(new WarehouseConfig(wh2Name, wh2Url, wh2User, wh2Pass));
        list.add(new WarehouseConfig(wh6Name, wh6Url, wh6User, wh6Pass));

        return list;
    }
}