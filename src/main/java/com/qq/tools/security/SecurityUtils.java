package com.qq.tools.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 密码加密工具类（提供MD5加密功能）
 */
public class SecurityUtils {

    /**
     * MD5加密方法
     *
     * @param plainPassword 原始密码
     * @return 32位小写MD5哈希值
     */
    public static String encryptPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            return "";
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(plainPassword.getBytes());

            // 将字节数组转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // 回退到简单哈希（实际生产环境应使用更安全的处理）
            System.err.println("MD5算法不可用，使用简单哈希: " + e.getMessage());
            return fallbackHash(plainPassword);
        }
    }

    /**
     * 安全增强：加盐的MD5加密
     *
     * @param plainPassword 原始密码
     * @param salt          盐值
     * @return 加盐后的MD5哈希值
     */
    public static String encryptPasswordWithSalt(String plainPassword, String salt) {
        return encryptPassword(salt + plainPassword);
    }

    /**
     * 生成随机盐值
     *
     * @return 8位随机盐值
     */
    public static String generateSalt() {
        return Long.toHexString(Double.doubleToLongBits(Math.random())).substring(0, 8);
    }

    /**
     * 回退哈希方法（当MD5不可用时）
     */
    private static String fallbackHash(String plainPassword) {
        return String.valueOf(plainPassword.hashCode());
    }

    /**
     * 验证密码（加盐版）
     *
     * @param inputPassword 用户输入的密码
     * @param salt          盐值
     * @param storedHash    存储的哈希值
     * @return 是否匹配
     */
    public static boolean validatePassword(String inputPassword, String salt, String storedHash) {
        return storedHash.equals(encryptPassword(salt + inputPassword));
    }

    /**
     * 验证密码（普通版）
     *
     * @param inputPassword 用户输入的密码
     * @param storedHash    存储的哈希值
     * @return 是否匹配
     */
    public static boolean validatePassword(String inputPassword, String storedHash) {
        return storedHash.equals(encryptPassword(inputPassword));
    }
}