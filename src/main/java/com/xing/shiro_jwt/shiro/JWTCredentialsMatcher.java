package com.xing.shiro_jwt.shiro;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.springframework.stereotype.Component;

@Component
public class JWTCredentialsMatcher implements CredentialsMatcher {
    @Override
    public boolean doCredentialsMatch(AuthenticationToken authenticationToken, AuthenticationInfo authenticationInfo) {
        //JWTRealm中可能返回Null
        if (authenticationInfo == null){
            return false;
        }
        try {
            JWTToken jwtToken = (JWTToken) authenticationToken;
            String token = jwtToken.getToken();
            //JWT加密的密钥是用户的盐值
            String salt = (String)authenticationInfo.getCredentials();
            boolean flag = JWTUtils.verify(token,salt);
            return flag;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
