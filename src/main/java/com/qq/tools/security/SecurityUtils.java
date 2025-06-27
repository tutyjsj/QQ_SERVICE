package com.qq.tools.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 安全工具类
 */
public class SecurityUtils {

    /**
     * MD5加密方法
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
            // 回退到简单哈希
            System.err.println("MD5算法不可用，使用简单哈希: " + e.getMessage());
            return fallbackHash(plainPassword);
        }
    }

    /**
     * 加盐的MD5加密
     */
    public static String encryptPasswordWithSalt(String plainPassword, String salt) {
        return encryptPassword(salt + plainPassword);
    }

    /**
     * 生成随机盐值
     */
    public static String generateSalt() {
        return Long.toHexString(Double.doubleToLongBits(Math.random())).substring(0, 8);
    }

    /**
     * 回退哈希方法
     */
    private static String fallbackHash(String plainPassword) {
        return String.valueOf(plainPassword.hashCode());
    }

    /**
     * 验证密码（加盐版）
     */
    public static boolean validatePassword(String inputPassword, String salt, String storedHash) {
        return storedHash.equals(encryptPassword(salt + inputPassword));
    }

    /**
     * 验证密码（普通版）
     */
    public static boolean validatePassword(String inputPassword, String storedHash) {
        return storedHash.equals(encryptPassword(inputPassword));
    }
}