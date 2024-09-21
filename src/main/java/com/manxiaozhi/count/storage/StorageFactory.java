package com.manxiaozhi.count.storage;

import java.util.Properties;

public class StorageFactory {
    public static Storage createStorage(String storageType) {
        Properties props = System.getProperties();
        switch (storageType.toLowerCase()) {
            case "jdbc":
                return createJdbcStorage(props);
            // 在这里可以添加其他存储类型的case
            default:
                throw new IllegalArgumentException("不支持的存储类型: " + storageType);
        }
    }

    private static Storage createJdbcStorage(Properties props) {
        String dbUrl = props.getProperty("db.url", "jdbc:mysql://localhost:3306/count");
        String dbUser = props.getProperty("db.user", "default_user");
        String dbPassword = props.getProperty("db.password", "default_password");
        return new JdbcStorage(dbUrl, dbUser, dbPassword);
    }

    // 可以添加其他存储类型的创建方法
}