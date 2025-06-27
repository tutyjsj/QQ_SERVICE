package com.qq.tools.security;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigUtil.class.getClassLoader()
                .getResourceAsStream("server.properties")) {
            properties.load(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getServerIp() {
        return properties.getProperty("server.ip", "60.223.147.56"); // 默认值
    }

    public static int getServerPort() {
        return Integer.parseInt(properties.getProperty("server.port", "1521")); // 默认值
    }
}