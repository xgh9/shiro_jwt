package com.xing.shiro_jwt.controller;

import com.xing.shiro_jwt.vo.JsonResponse;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ErrorController {

    @ApiOperation("token认证失败")
    @RequestMapping(value = "/notToken")
    public JsonResponse notToken() {
        return JsonResponse.tokenError();
    }
}
