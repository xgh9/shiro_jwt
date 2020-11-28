package com.xing.shiro_jwt.dao;

import com.xing.shiro_jwt.vo.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    Integer insert(User user);

    Integer checkExist(String id);

    User getUserById(String id);

    Integer update(User user);

    Integer checkAdmin();

    Integer delete(String id);

    Integer batchRegister(List<User> users);

    List<User> getAllUsers();

    List<User> getAllAdmins();

    List<User> getAllStudents();
}
