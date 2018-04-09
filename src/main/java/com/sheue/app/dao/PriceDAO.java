package com.sheue.app.dao;

import com.sheue.app.bean.Data;
import com.sheue.app.db.JDBCConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PriceDAO {

    private static Connection con = JDBCConnection.getConnection();

    public static void insert(Data data) {
        String sql = "insert into price (name,average_price,date) values (?,?,?)";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, data.getName());
            ps.setDouble(2, data.getPrice());
            ps.setString(3, String.valueOf(data.getDate()));
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void delete(Data data) {
        String sql = "delete from price where id=" + data.getId();
        try {
            con.prepareStatement(sql).execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String has(Data data) {
        String sql = "select * from price where id=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, data.getId());
            ResultSet rs = ps.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "ok";
    }

    public static List<String> getAll() {
        String sql = "select distinct(name) name from price";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            List<String> list = new ArrayList<String>();
            while (rs.next()) {
                list.add(rs.getString("name"));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getTotal(String name) {
        String sql = "select count(1) count from price where name=?";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return (rs.getInt("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getTotal() {
        String sql = "select count(1) count from price";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return (rs.getInt("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static List<Data> getPage(String name, int page) {
        String sql = "select * from price where name=? order by date desc limit ?,10";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ps.setInt(2, 10 * (page - 1));
            ResultSet rs = ps.executeQuery();
            List<Data> list = new ArrayList<Data>();
            while (rs.next()) {
                Data data = new Data();
                data.setId(rs.getInt("id"));
                data.setName(rs.getString("name"));
                data.setPrice(rs.getDouble("average_price"));
                data.setStandard(rs.getString("standard"));
                String str = rs.getString("date");
                Date date = new SimpleDateFormat("yy-MM-dd").parse(str);
                data.setDate(date);

                list.add(data);
            }
            return list;
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Data> getPage(int page) {
        String sql = "select * from price order by date desc limit ?,10";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, 10 * (page - 1));
            ResultSet rs = ps.executeQuery();
            List<Data> list = new ArrayList<Data>();
            while (rs.next()) {
                Data data = new Data();
                data.setId(rs.getInt("id"));
                data.setName(rs.getString("name"));
                data.setPrice(rs.getDouble("average_price"));
                data.setStandard(rs.getString("standard"));
                String str = rs.getString("date");
                Date date = new SimpleDateFormat("yy-MM-dd").parse(str);
                data.setDate(date);

                list.add(data);
            }
            return list;
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Data> getTrain(String name) {
        String sql = "select * from price where name=? order by date";
//        String sql = "select * from fruit_price f where DATE_FORMAT(f.date,'%Y-%m-%d') >=\n" +
//                " '2017-01-01' order by date";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            List<Data> list = new ArrayList<Data>();
            while (rs.next()) {
                Data data = new Data();
                data.setId(rs.getInt("id"));
                data.setName(rs.getString("name"));
                data.setPrice(rs.getDouble("average_price"));
                String str = rs.getString("date");
                Date date = new SimpleDateFormat("yy-MM-dd").parse(str);
                data.setDate(date);

                list.add(data);
            }
            return list;
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Data> getTest(String name, int number) {
        String sql = "select * from price where name=? order by date desc limit ?";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ps.setInt(2, number);
            ResultSet rs = ps.executeQuery();
            List<Data> list = new ArrayList<Data>();
            while (rs.next()) {
                Data data = new Data();
                data.setId(rs.getInt("id"));
                data.setName(rs.getString("name"));
                data.setPrice(rs.getDouble("average_price"));
                String str = rs.getString("date");
                Date date = new SimpleDateFormat("yy-MM-dd").parse(str);
                data.setDate(date);

                list.add(data);
            }
            return list;
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

}
