package com.xing.shiro_jwt.exception;

import com.xing.shiro_jwt.vo.JsonResponse;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;


/**
 * controller抛出的异常会在这被处理，但过滤器中的异常无法处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    //登陆异常
    @ExceptionHandler(value = {org.apache.shiro.authz.UnauthenticatedException.class})
    public JsonResponse notLoginError(UnauthenticatedException e){
        log.error(e.toString());
        return JsonResponse.noLogError();
    }

    //权限异常
    @ExceptionHandler(value = {AuthorizationException.class})
    public JsonResponse notAdminError(AuthorizationException e){
        log.error(e.toString());
        return JsonResponse.noAuthority();
    }

    //文件下载时文件不存在异常
    @ExceptionHandler(value = {FileNotFoundException.class})
    public JsonResponse FileNotFoundError(FileNotFoundException e){
        log.error(e.toString());
        return JsonResponse.invalidParam("文件不存在!");
    }

    //认证异常，登录认证异常在service中处理了，过滤器Token认证异常不会到这里，应该不会被调用
    @ExceptionHandler(value = {AuthenticationException.class})
    public JsonResponse catchAuthenticationException(AuthenticationException e){
        log.error(e.toString());
        return JsonResponse.invalidParam("登陆失败！");
    }

//    @ExceptionHandler(value = {Exception.class})
    public JsonResponse error(HttpServletRequest request, Exception e){
        log.error(e.toString());
        return JsonResponse.error(getStatus(request).value(),e.getMessage());
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }
}
