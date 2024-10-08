package com.manxiaozhi.count.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

public class JdbcStorage implements Storage {
    private HikariDataSource dataSource;

    public JdbcStorage(String dbUrl, String dbUser, String dbPassword) {
        HikariConfig config = new HikariConfig();
        String urlSetting = "useLegacyDatetimeCode=false&serverTimezone=Asia/Shanghai&characterEncoding=UTF-8";
        config.setJdbcUrl(dbUrl + "?" + urlSetting);
        config.setUsername(dbUser);
        config.setPassword(dbPassword);
        config.setMaximumPoolSize(10);
        dataSource = new HikariDataSource(config);
    }

    public void saveMetric(String metricName, JSONObject dimension) throws Exception {
        String sql = "INSERT INTO metrics (metric_name, timestamp, dimensions) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, metricName);
            pstmt.setTimestamp(2, new Timestamp(dimension.getLong("timestamp")));
            
            JSONObject dimensionsCopy = new JSONObject(dimension.toString());
            dimensionsCopy.remove("timestamp");
            pstmt.setString(3, dimensionsCopy.toString());
            
            pstmt.executeUpdate();
        }
    }

    public void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
