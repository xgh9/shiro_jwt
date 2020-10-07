package com.xing.shiro_jwt.exception;

import com.xing.shiro_jwt.vo.JsonResponse;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {org.apache.shiro.authz.UnauthenticatedException.class})
    public JsonResponse notLoginError(Exception e){
        return JsonResponse.noLogError();
    }

    @ExceptionHandler(value = {AuthorizationException.class})
    public JsonResponse notAdminError(Exception e){
        return JsonResponse.noAuthority(e.getMessage());
    }

    //已知token错误会抛出该异常，其他未知
    @ExceptionHandler(value = {AuthenticationException.class})
    public JsonResponse catchAuthenticationException(HttpServletRequest request, AuthenticationException e){
        return JsonResponse.invalidParam("登陆失败！");
    }

    @ExceptionHandler(value = {Exception.class})
    public JsonResponse error(HttpServletRequest request, Exception e){
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
