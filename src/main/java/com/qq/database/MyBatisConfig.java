package com.qq.database;

public class MyBatisConfig {
}
//package com.qq.config;
//
//import org.apache.ibatis.io.Resources;
//import org.apache.ibatis.session.SqlSession;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.apache.ibatis.session.SqlSessionFactoryBuilder;
//
//import java.io.IOException;
//import java.io.InputStream;
//
//public class MyBatisConfig {
//    private static SqlSessionFactory sqlSessionFactory;
//
//    static {
//        try {
//            String resource = "mybatis-config.xml";
//            InputStream inputStream = Resources.getResourceAsStream(resource);
//            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
//        } catch (IOException e) {
//            throw new RuntimeException("MyBatis初始化失败", e);
//        }
//    }
//
//    public static SqlSession getSqlSession() {
//        return sqlSessionFactory.openSession(true); // 自动提交事务
//    }
//}