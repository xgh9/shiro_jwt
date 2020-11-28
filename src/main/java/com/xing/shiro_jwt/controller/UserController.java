package com.xing.shiro_jwt.controller;

import com.xing.shiro_jwt.service.UserService;
import com.xing.shiro_jwt.shiro.JWTUtils;
import com.xing.shiro_jwt.vo.ConstantField;
import com.xing.shiro_jwt.vo.JsonResponse;
import com.xing.shiro_jwt.vo.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 需要登录后才能操作的方法，admin和student都可以
 */
@RestController
@Api(tags = "用户操作")
public class UserController {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserService userService;

    /**
     *  "id":"admin","password":"123456"
     * @param params
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("登录")
    public JsonResponse login(@RequestBody Map<String, String> params, HttpServletResponse httpServletResponse){
        String id = params.get(ConstantField.ID);
        String password = params.get(ConstantField.PASSWORD);
        if (StringUtils.isEmpty(id)){
            return JsonResponse.invalidParam("学号呢？");
        }
        if (StringUtils.isEmpty(password)){
            return JsonResponse.invalidParam("密码呢？");
        }
        //将token放入响应头
        JsonResponse jsonResponse = userService.login(id,password);

        String token = (String)jsonResponse.get(ConstantField.TOKEN);
        jsonResponse.remove(ConstantField.TOKEN);

        httpServletResponse.setHeader(ConstantField.TOKEN, token);
        httpServletResponse.setHeader("Access-Control-Expose-Headers", ConstantField.TOKEN);
        return jsonResponse;
    }

    @PostMapping("/register")
    @ApiOperation("注册")
    public JsonResponse register(@RequestBody User user){
        if (StringUtils.isEmpty(user.getId())){
            return JsonResponse.invalidParam("学号呢？");
        }
        if (StringUtils.isEmpty(user.getName())){
            return JsonResponse.invalidParam("姓名呢？");
        }
        if (StringUtils.isEmpty(user.getPassword())){
            return JsonResponse.invalidParam("密码呢？");
        }
        if (userService.checkExist(user.getId()) > 0){
            return JsonResponse.invalidParam("学号" + user.getId() + "已经被别人抢先一步使用了，如果这是你的学号，快到助教这来找回账户！");
        }
        //数据库没有管理员账号时提供注册管理员账号的方法
        if (ConstantField.ROLE_ADMIN.equals(user.getRole())){
            if (userService.checkAdmin() == 0){
                return userService.register(user);
            }else {
                return JsonResponse.invalidParam("数据库中已有管理员账号，若丢失密码可以重新导入sql文件或手动将MD5盐值加密并hash10次的密码写入数据库！");
            }
        }
        user.setRole(ConstantField.ROLE_STUDENT);
        return userService.register(user);
    }

    @GetMapping("/logout")
    @ApiOperation("注销")
    @RequiresAuthentication
    public JsonResponse logout(){
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return JsonResponse.success();
    }

    @PostMapping("/changePassword")
    @ApiOperation("修改密码")
    @RequiresAuthentication
    public JsonResponse changePassowrd(@RequestBody Map<String, String> params){
        String oldPassword = params.get(ConstantField.OLD_PASSWORD);
        String password = params.get(ConstantField.PASSWORD);

        if (StringUtils.isEmpty(oldPassword)){
            return JsonResponse.invalidParam("旧密码呢？");
        }
        if (StringUtils.isEmpty(password)){
            return JsonResponse.invalidParam("新密码呢？");
        }
        JsonResponse jsonResponse = userService.changePassowrd(oldPassword,password);
        return jsonResponse;
    }

    @PostMapping("/changeName")
    @ApiOperation("修改姓名")
    @RequiresAuthentication
    public JsonResponse changeName(@RequestBody String name){
        return userService.changeName(name);
    }


    @PostMapping("/deleteUser")
    @ApiOperation("删除用户")
    @RequiresRoles("admin")
    public JsonResponse delete(@RequestBody String id) {
        JsonResponse jsonResponse = userService.delete(id);

        log.info(SecurityUtils.getSubject().getPrincipal() + "删除" + id);

        return jsonResponse;
    }

    @PostMapping("/registerAdmin")
    @ApiOperation("注册管理员账号")
    @RequiresRoles("admin")
    public JsonResponse registerAdmin(@RequestBody User user){
        if (StringUtils.isEmpty(user.getId())){
            return JsonResponse.invalidParam("管理员账号呢？");
        }
        if (StringUtils.isEmpty(user.getName())){
            user.setName(user.getId());
        }
        if (userService.checkExist(user.getId()) > 0){
            return JsonResponse.invalidParam("帐号" + user.getId() + "已经被别人抢先一步使用了，如果这是你的学号，快到助教这来找回账户！");
        }
        if (StringUtils.isEmpty(user.getPassword())){
            return JsonResponse.invalidParam("密码呢？");
        }
        user.setRole(ConstantField.ROLE_ADMIN);
        userService.register(user);
        return JsonResponse.success();
    }

    @PostMapping("/batchRegister")
    @ApiOperation("批量注册")
    @RequiresRoles("admin")
    public JsonResponse batchRegister(@RequestBody Map<String, String> params) {
        long start, end;
        try {
            start = Long.valueOf(params.get("start"));
            end = Long.valueOf(params.get("end"));
        }catch (Exception e){
            return JsonResponse.invalidParam("参数无效！");
        }
        if (end < start){
            return JsonResponse.invalidParam("参数无效！");
        }
        return userService.batchRegister(start,end);
    }

    @GetMapping("/getUsers")
    @ApiOperation("获取所有用户")
    @RequiresRoles("admin")
    public JsonResponse getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/getAdmins")
    @ApiOperation("获取所有管理员")
    @RequiresRoles("admin")
    public JsonResponse getAdmins() {
        return userService.getAllAdmins();
    }

    @GetMapping("/getStudents")
    @ApiOperation("获取所有学生")
    @RequiresRoles("admin")
    public JsonResponse getStudents() {
        return userService.getAllStudents();
    }
    
}
