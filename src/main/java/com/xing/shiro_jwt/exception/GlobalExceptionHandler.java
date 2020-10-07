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


/**
 * controller抛出的异常会在这被处理，但过滤器中的异常无法处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(value = {org.apache.shiro.authz.UnauthenticatedException.class})
    public JsonResponse notLoginError(UnauthenticatedException e){
        log.error(e.toString());
        return JsonResponse.noLogError();
    }

    @ExceptionHandler(value = {AuthorizationException.class})
    public JsonResponse notAdminError(AuthorizationException e){
        log.error(e.toString());
        return JsonResponse.noAuthority();
    }

    //认证异常，登录认证异常在service中处理了，过滤器Token认证异常不会到这里，应该不会被调用
    @ExceptionHandler(value = {AuthenticationException.class})
    public JsonResponse catchAuthenticationException(AuthenticationException e){
        log.error(e.toString());
        return JsonResponse.invalidParam("登陆失败！");
    }

    @ExceptionHandler(value = {Exception.class})
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
