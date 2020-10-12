package com.xing.shiro_jwt.service;


import com.xing.shiro_jwt.vo.JsonResponse;
import com.xing.shiro_jwt.vo.User;

public interface UserService {

    //登录
    JsonResponse login(String id, String password);

    //注册
    JsonResponse register(User user);

    //学号验重
    int checkExist(String id);

    //根据学号获取user
    User getUserById(String id);

    //修改密码
    JsonResponse changePassowrd(String oldPassword, String password);

    //获取当前角色
    String getRole();

    //检查数据库中是否有管理员账户，没有管理员账户不需要管理员权限也能注册管理员账户
    Integer checkAdmin();

    //删除用户
    JsonResponse delete(String id);
}
