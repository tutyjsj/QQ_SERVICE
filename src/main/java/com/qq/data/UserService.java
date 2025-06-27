package com.qq.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService {
    // 用户登录验证
    public boolean checkUser(String userId, String password, String serverIp, int serverPort) {
        String sql = "SELECT * FROM users WHERE user_id = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // 更新用户状态为在线
                    updateUserStatus(userId, "online");
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("用户验证失败: " + e.getMessage());
        }
        return false;
    }

    // 更新用户状态
    public void updateUserStatus(String userId, String status) {
        String sql = "UPDATE users SET status = ? WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setString(2, userId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("更新用户状态失败: " + e.getMessage());
        }
    }

    // 获取在线用户列表
    public String getOnlineUsers() {
        StringBuilder onlineUsers = new StringBuilder();
        String sql = "SELECT user_id FROM users WHERE status = 'online'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                onlineUsers.append(rs.getString("user_id")).append(" ");
            }

        } catch (SQLException e) {
            System.err.println("获取在线用户失败: " + e.getMessage());
        }

        return onlineUsers.toString().trim();
    }
}