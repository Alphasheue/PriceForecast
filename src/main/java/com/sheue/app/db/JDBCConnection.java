package com.sheue.app.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCConnection {
    private final static String URL = "jdbc:mysql://127.0.0.1:3306/hdfs?useUnicode=true&characterEncoding=UTF-8";
    private final static String USER = "root";
    private final static String PASSWORD = "123456";
    private static Connection conn = null;

    private static JDBCConnection connection;

    private JDBCConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        if (connection == null) {
            synchronized (JDBCConnection.class) {
                if (connection == null) {
                    connection = new JDBCConnection();
                }
            }
        }
        return conn;
    }

}
