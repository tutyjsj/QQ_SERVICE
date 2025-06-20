package com.qq.Mapper;

import com.qq.qqcommon.User;

public interface UserMapper {
    // 根据用户ID查询用户
    User selectUserById(String userId);

    // 验证用户登录
    User validateUser(String userId, String password);

    // 插入新用户
    int insertUser(User user);

    // 更新用户状态
    int updateUserStatus(User user);

    // 更新最后登录时间
    int updateLastLoginTime(String userId);
}