package com.cinebook.db;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public final class SqlScriptRunner {
    private SqlScriptRunner() {}

    public static void run(Connection connection, String resourcePath) throws Exception {
        List<String> statements = loadStatements(resourcePath);
        try (Statement statement = connection.createStatement()) {
            for (String sql : statements) {
                statement.execute(sql);
            }
        }
    }

    private static List<String> loadStatements(String resourcePath) throws Exception {
        InputStream input = SqlScriptRunner.class.getClassLoader().getResourceAsStream(resourcePath);
        if (input == null) {
            throw new IllegalStateException("SQL resource not found: " + resourcePath);
        }

        List<String> statements = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("--") || trimmed.startsWith("//")) {
                    continue;
                }
                builder.append(line).append('\n');
            }
        }

        String[] parts = builder.toString().split(";");
        for (String part : parts) {
            String sql = part.trim();
            if (!sql.isEmpty()) {
                statements.add(sql);
            }
        }
        return statements;
    }
}
