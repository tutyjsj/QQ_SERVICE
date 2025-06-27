package com.qq.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class FileService {
    // 保存文件记录
    public void saveFileRecord(String fileId, String fileName, long fileSize,
                               String senderId, String receiverId, String storagePath) {
        String sql = "INSERT INTO files (file_id, file_name, file_size, sender_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 提取文件类型
            String fileType = "unknown";
            if (fileName.contains(".")) {
                fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
            }

            pstmt.setString(1, fileId);
            pstmt.setString(2, fileName);
            pstmt.setLong(3, fileSize);
            pstmt.setString(4, fileType);
            pstmt.setString(5, senderId);
            pstmt.setString(6, receiverId);
            pstmt.setString(7, storagePath);
            pstmt.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("保存文件记录失败: " + e.getMessage());
        }
    }

    // 获取文件路径
    public String getFilePath(String fileId) {
        String sql = "SELECT  *FROM files WHERE file_id = ?";
        String path = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fileId);
            try (var rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    path = rs.getString("storage_path");
                }
            }

        } catch (SQLException e) {
            System.err.println("获取文件路径失败: " + e.getMessage());
        }

        return path;
    }
}