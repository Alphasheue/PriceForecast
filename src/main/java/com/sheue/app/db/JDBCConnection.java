package com.sheue.app.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCConnection {
    private final static String URL = "jdbc:mysql://127.0.0.1:3306/hdfs?useUnicode=true&characterEncoding=UTF-8";
    private final static String USER = "root";
    private final static String PASSWORD = "123456";
    private static Connection conn;

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("ClassNotFoundException:" + e.getMessage());
        } catch (SQLException e) {
            System.err.println("SQLException:" + e.getMessage());
        }
    }

    public static Connection getConnection() {
        return conn;
    }
}
