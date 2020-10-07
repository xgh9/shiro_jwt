package com.xing.shiro_jwt.controller;

import com.xing.shiro_jwt.service.ShiroService;
import com.xing.shiro_jwt.vo.ConstantField;
import com.xing.shiro_jwt.vo.JsonResponse;
import com.xing.shiro_jwt.vo.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 需要管理员权限的操作
 */
@RestController
@RequestMapping("/admin")
@RequiresRoles("admin")
@Api(tags = "管理员操作")
public class AdminController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ShiroService shiroService;

    @PostMapping("/delete")
    @ApiOperation("删除用户")
    public JsonResponse delete(@RequestBody String id) {
        JsonResponse jsonResponse = shiroService.delete(id);

        log.info(SecurityUtils.getSubject().getPrincipal() + "删除" + id);

        return jsonResponse;
    }

    @PostMapping("/register")
    @ApiOperation("注册管理员账号")
    public JsonResponse register(User user){
        if (StringUtils.isEmpty(user.getId())){
            return JsonResponse.invalidParam("管理员账号呢？");
        }
        if (shiroService.checkExist(user.getId()) > 0){
            return JsonResponse.invalidParam("帐号" + user.getId() + "已经被别人抢先一步使用了，如果这是你的学号，快到助教这来找回账户！");
        }
        if (StringUtils.isEmpty(user.getPassword())){
            return JsonResponse.invalidParam("密码呢？");
        }

        user.setRole(ConstantField.ROLE_ADMIN);
        shiroService.register(user);
        return JsonResponse.success();
    }
}
