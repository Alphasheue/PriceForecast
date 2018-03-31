package com.sheue.app.dao;

import com.sheue.app.bean.User;
import com.sheue.app.db.JDBCConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    private static Connection con = JDBCConnection.getConnection();

    public static String add(User u) {
        String sql = "insert into user (account,name,password) values (?,?,?)";

        try {
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, u.getAccount());
            ps.setString(2, u.getName());
            ps.setString(3, u.getPassword());

            ps.execute();
        } catch (SQLException e) {
            return e.getMessage();
        }
        return "ok";
    }

    public static String delete(String account) {
        String sql = "delete from user where account=" + account;
        try {
            con.prepareStatement(sql).execute();
        } catch (SQLException e) {
            return e.getMessage();
        }
        return "ok";
    }

    public static String has(User u) {
        String sql = "select * from user where account=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, u.getAccount());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                if (!rs.getString("password").equals(u.getPassword())) {
                    return "wrong";
                }
            } else {
                return "empty";
            }
        } catch (SQLException e) {
            return e.getMessage();
        }

        return "ok";
    }

    public static String get(User u) {
        String sql = "select * from user where name=?";

        try {
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, u.getName());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("account");
            }

        } catch (SQLException e) {
            return e.getMessage();
        }
        return "ok";
    }

}
