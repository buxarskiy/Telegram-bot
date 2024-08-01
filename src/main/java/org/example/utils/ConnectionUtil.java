package org.example.utils;

import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionUtil {
    private static Connection connection;

    @SneakyThrows
    public static Connection getConnection() {
        if (connection == null) {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/vobkent_uz_db",
                    "postgres",
                    "joxa77"
            );
        }
        return connection;
    }
}
