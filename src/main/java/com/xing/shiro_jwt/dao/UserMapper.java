package com.xing.shiro_jwt.dao;

import com.xing.shiro_jwt.vo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    Integer insert(User user);

    Integer checkExist(String id);

    User getUserById(String id);

    Integer update(User user);

    Integer checkAdmin();

    Integer delete(String id);
}