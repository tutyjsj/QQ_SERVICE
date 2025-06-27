package com.qq.tools.security;
/**
 * 密码编码器接口
 */

public interface PasswordEncoder {
    /**
     * * 加密原始密码
     * * @param rawPassword 原始密码
     * * @return 加密后的密码
     * */
    String encode(CharSequence rawPassword);

    /**
     * * 验证密码是否匹配
     * * @param rawPassword 原始密码
     * * @param encodedPassword 加密后的密码
     * * @return 是否匹配
     * */
    boolean matches(CharSequence rawPassword, String encodedPassword);

    /**
     * * 升级密码编码（如果需要）
     * * @param encodedPassword 加密后的密码
     * * @return 是否需要升级
     * */
    default boolean upgradeEncoding(String encodedPassword) {
        return false;
    }
}
