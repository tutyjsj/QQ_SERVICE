package com.qq.data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class MessageService {
//    getterId
    // 保存私聊消息
    public void savePrivateMessage(String senderId, String receiverId, String content) {
        String sql = "INSERT INTO messages (sender_id, receiver_id, content, message_type) " +
                "VALUES (?, ?, ?, 'private', 'sent', ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, senderId);
            pstmt.setString(2, receiverId);
            pstmt.setString(3, content);
            pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("保存私聊消息失败: " + e.getMessage());
        }
    }

    // 保存群发消息
    public void saveGroupMessage(String senderId, String content) {
        String sql = "INSERT INTO messages (sender_id, content, message_type) " +
                "VALUES (?, ?, 'group', 'sent', ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, senderId);
            pstmt.setString(2, content);
            pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("保存群发消息失败: " + e.getMessage());
        }
    }
}