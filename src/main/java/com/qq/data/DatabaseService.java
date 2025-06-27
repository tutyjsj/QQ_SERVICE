package com.qq.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 数据库连接服务
 */
public class DatabaseService {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/qq_db";
    private static final String DB_USER = "qq_user";
    private static final String DB_PASSWORD = "qq_password";

    static {
        try {
            // 加载JDBC驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC 驱动加载成功");
        } catch (ClassNotFoundException e) {
            System.err.println("找不到MySQL JDBC驱动");
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库连接
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

}