package com.xing.shiro_jwt.config;

import com.xing.shiro_jwt.service.ShiroService;
import com.xing.shiro_jwt.util.JWTUtils;
import com.xing.shiro_jwt.vo.User;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

public class JWTRealm extends AuthorizingRealm {

    @Autowired
    ShiroService shiroService;

    /**
     * 如果subject.login(token)传过来的token不是JWTToken就跳过这个reaml，非常巧妙
     * @param token
     * @return
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

    /**
     * 我们在JWTToken中重写了
     * PrincipalCollection中放的就是JWTToken
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        Object primaryPrincipal = principalCollection.getPrimaryPrincipal();
        System.out.println(primaryPrincipal);
        return null;
    }

    /**
     * 请求头中携带token的会在JWTFilter中进行认证，最后找到JWTRealm的这个方法
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        try {
            JWTToken jwtToken = (JWTToken) authenticationToken;
            String token = jwtToken.getToken();
            User user = JWTUtils.getClaim(token);
            if (user != null && user.getId() != null){
                user = shiroService.getUserById(user.getId());
                return new SimpleAuthenticationInfo(user.getId(),user.getSalt(),"JWTRealm");
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
