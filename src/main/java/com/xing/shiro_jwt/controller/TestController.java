package com.xing.shiro_jwt.controller;

import com.xing.shiro_jwt.config.JWTToken;
import com.xing.shiro_jwt.vo.ConstantField;
import com.xing.shiro_jwt.vo.JsonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Api(tags = "测试")
public class TestController {

    @GetMapping("user")
    @ApiOperation("需要登录")
    @RequiresAuthentication
    public JsonResponse user(){
        return JsonResponse.success();
    }

    @GetMapping("admin")
    @ApiOperation("需要登录")
    @RequiresRoles("admin")
    public JsonResponse admin(){
        return JsonResponse.success();
    }

    @GetMapping("/testToken")
    @ApiOperation("测试token")
    public JsonResponse testToken(String token){

        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        subject.login(new JWTToken(token));
        if (subject.isAuthenticated()){
            return JsonResponse.success();
        }else {
            return JsonResponse.unknownError();
        }
    }

    @GetMapping("/auth")
    @ApiOperation("获取登录及权限信息")
    public JsonResponse test(){
        boolean falgLog = SecurityUtils.getSubject().isAuthenticated();
        boolean flagAdmin = SecurityUtils.getSubject().hasRole(ConstantField.ROLE_ADMIN);
        JsonResponse success = JsonResponse.success();
        success.put("登陆状态",falgLog);
        success.put("管理员权限",flagAdmin);
        Subject subject = SecurityUtils.getSubject();
        boolean remembered = subject.isRemembered();
        success.put("记住我",remembered);
        return success;
    }

}
