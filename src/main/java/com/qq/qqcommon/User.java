package com.qq.qqcommon;

import java.io.Serializable;

/**
 * 表示一个用户/客户信息
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    private String userId;//用户Id/用户名
    private String passwd;//用户密码

    public User() {}
    public User(String userId, String passwd) {
        this.userId = userId;
        this.passwd = passwd;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
}
//
///**
// * 用户层
// * 用于存储用户账号密码信息的
// */
//public class User implements Serializable {
//    private static final long serialVersionUID = 1L;
//    private String userId;  // 用户Id/用户名
//    private String password; // 用户密码
//
//    public User() {}
//
//    public User(String userId, String password) {
//        this.userId = userId;
//        this.password = password;
//    }
//
//    public String getUserId() {
//        return userId;
//    }
//
//    public void setUserId(String userId) {
//        this.userId = userId;
//    }
//
//    /**
//     * 设置密码时自动进行MD5加密
//     */
//    public void setPassword(String plainPassword) {
//        this.password = SecurityUtils.encryptPassword(plainPassword);
//    }
//    public String getPassword() {
//        return password;
//    }
//
//    /**
//     * 验证密码是否正确
//     */
//    public boolean validatePassword(String inputPassword) {
//        return SecurityUtils.validatePassword(inputPassword, this.password);
//    }
//
//    @Override
//    public String toString() {
//        // 注意：这里硬编码了密码显示为"123456"，这可能不是最佳实践
//        return "User [userId=" + userId + ", password=" + "******" + "]";
//    }
//}