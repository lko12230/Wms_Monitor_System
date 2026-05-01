package com.wms.monitor.entity;

public class WarehouseConfig {

    private String name;
    private String url;
    private String username;
    private String password;

    public WarehouseConfig(String name, String url, String username, String password) {
        this.name = name;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public String getName() { return name; }
    public String getUrl() { return url; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}