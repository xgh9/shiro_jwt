package com.xing.shiro_jwt.service;

import com.xing.shiro_jwt.dao.UserMapper;
import com.xing.shiro_jwt.vo.ConstantField;
import com.xing.shiro_jwt.vo.JsonResponse;
import com.xing.shiro_jwt.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

@Service
public class AdminServiceImpl  implements AdminService{

    @Resource
    UserMapper userMapper;

    @Override
    public JsonResponse createClass(String clazz) {

        return null;
    }
}
