package com.xing.shiro_jwt.vo;

import com.google.common.collect.Maps;
import com.xing.shiro_jwt.service.ShiroService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
public class JsonResponse extends HashMap<String,Object>{
    public static Map<Integer,String> error = Maps.newHashMap();

    public static final String ERROR_MSG = "errorMsg";

    public static final String ERROR_CODE = "errorCode";

    @Resource
    ShiroService shiroService;

    public static JsonResponse staticJsonResponse;

    private JsonResponse(){}

    @PostConstruct
    public void init(){
        staticJsonResponse = this;
        staticJsonResponse.shiroService = this.shiroService;
    }

    public static JsonResponse error(int code, String msg){
        JsonResponse jsonResponse = new JsonResponse();
        jsonResponse.put(ERROR_CODE,code);
        jsonResponse.put(ERROR_MSG,msg);
        jsonResponse.put("role",staticJsonResponse.shiroService.getRole());
        return jsonResponse;
    }
    public static JsonResponse success(){
        JsonResponse jsonResponse = new JsonResponse();
        jsonResponse.put(ERROR_CODE,0);
        jsonResponse.put(ERROR_MSG,"操作成功");
        jsonResponse.put("role",staticJsonResponse.shiroService.getRole());
        return jsonResponse;
    }

    public static JsonResponse noLogError(){
        JsonResponse jsonResponse = new JsonResponse();
        jsonResponse.put(ERROR_CODE,1);
        jsonResponse.put(ERROR_MSG,"未登录");
        jsonResponse.put("role",staticJsonResponse.shiroService.getRole());
        return jsonResponse;
    }

    public static JsonResponse noAuthority(){
        JsonResponse jsonResponse = new JsonResponse();
        jsonResponse.put(ERROR_CODE,2);
        jsonResponse.put(ERROR_MSG,"没有权限");
        jsonResponse.put("role",staticJsonResponse.shiroService.getRole());
        return jsonResponse;
    }
    public static JsonResponse noAuthority(String msg){
        JsonResponse jsonResponse = new JsonResponse();
        jsonResponse.put(ERROR_CODE,2);
        jsonResponse.put(ERROR_MSG,msg);
        jsonResponse.put("role",staticJsonResponse.shiroService.getRole());
        return jsonResponse;
    }

    public static JsonResponse invalidParam(){
        JsonResponse jsonResponse = new JsonResponse();
        jsonResponse.put(ERROR_CODE,3);
        jsonResponse.put(ERROR_MSG,"参数错误");
        jsonResponse.put("role",staticJsonResponse.shiroService.getRole());
        return jsonResponse;
    }

    public static JsonResponse invalidParam(String msg){
        JsonResponse jsonResponse = new JsonResponse();
        jsonResponse.put(ERROR_CODE,3);
        jsonResponse.put(ERROR_MSG,msg);
        jsonResponse.put("role",staticJsonResponse.shiroService.getRole());
        return jsonResponse;
    }

    public static JsonResponse repeatLogin(String username){
        JsonResponse jsonResponse = new JsonResponse();
        jsonResponse.put(ERROR_CODE,4);
        jsonResponse.put(ERROR_MSG,"用户"+ username + "已登录，若要切换用户请先退出");
        jsonResponse.put("role",staticJsonResponse.shiroService.getRole());
        return jsonResponse;
    }

    public static JsonResponse unknownError(){
        JsonResponse jsonResponse = new JsonResponse();
        jsonResponse.put(ERROR_CODE,10);
        jsonResponse.put(ERROR_MSG,"未知错误");
        jsonResponse.put("role",staticJsonResponse.shiroService.getRole());
        return jsonResponse;
    }

    public static JsonResponse unknownError(String msg){
        JsonResponse jsonResponse = new JsonResponse();
        jsonResponse.put(ERROR_CODE,10);
        jsonResponse.put(ERROR_MSG, msg);
        jsonResponse.put("role",staticJsonResponse.shiroService.getRole());
        return jsonResponse;
    }
}
