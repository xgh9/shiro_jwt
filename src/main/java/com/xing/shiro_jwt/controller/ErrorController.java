package com.xing.shiro_jwt.controller;

import com.xing.shiro_jwt.vo.JsonResponse;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ErrorController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @ApiOperation("未登录")
    @GetMapping(value = "/notLogin")
    public JsonResponse notLogin() {
        System.out.println("/notLogin");
        return JsonResponse.noLogError();
    }

    @ApiOperation("无管理员权限")
    @GetMapping(value = "/notAdmin")
    public JsonResponse notAdmin() {
        return JsonResponse.noAuthority();
    }

    @ApiOperation("token认证失败")
    @GetMapping(value = "/notToken")
    public JsonResponse notToken() {
        return JsonResponse.tokenError();
    }
}
