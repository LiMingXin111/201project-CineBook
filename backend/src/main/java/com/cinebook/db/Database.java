package com.cinebook.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public final class Database {
    private static HikariDataSource dataSource;

    private Database() {}

    public static synchronized void init() {
        if (dataSource != null) {
            return;
        }

        Properties properties = new Properties();
        try (InputStream input = Database.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load db.properties", e);
        }

        String jdbcUrl = valueOrDefault("DB_URL", properties.getProperty("db.url"));
        String user = valueOrDefault("DB_USER", properties.getProperty("db.user"));
        String password = valueOrDefault("DB_PASSWORD", properties.getProperty("db.password"));
        String driver = valueOrDefault("DB_DRIVER", properties.getProperty("db.driver"));
        String poolSizeValue = valueOrDefault("DB_POOL_SIZE", properties.getProperty("db.pool.size"));
        int poolSize = parseInt(poolSizeValue, 10);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(user);
        config.setPassword(password);
        config.setDriverClassName(driver);
        config.setMaximumPoolSize(poolSize);
        config.setPoolName("CineBookPool");

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            init();
        }
        return dataSource.getConnection();
    }

    public static synchronized void shutdown() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
    }

    private static String valueOrDefault(String envKey, String fallback) {
        String value = System.getenv(envKey);
        if (value != null && !value.trim().isEmpty()) {
            return value.trim();
        }
        return fallback;
    }

    private static int parseInt(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
