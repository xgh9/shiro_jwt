package com.xing.shiro_jwt.shiro;

import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.pam.FirstSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.Arrays;
import java.util.LinkedHashMap;

@Configuration
public class ShiroConfig {

    //关系  ShiroFilterFactoryBean<-DefaultWebSecurityManager<-MyRealm<-HashedCredentialsMatcher
    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(DefaultWebSecurityManager defaultWebSecurityManager){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(defaultWebSecurityManager);
        // setLoginUrl 如果不设置值，默认会自动寻找Web工程根目录下的"/login.jsp"页面 或 "/login" 映射
        shiroFilterFactoryBean.setLoginUrl("/notLogin");
        // 设置无权限时跳转的 url;
//        shiroFilterFactoryBean.setUnauthorizedUrl("/notAdmin");

        // 在 Shiro过滤器链上加入 JWTFilter
        LinkedHashMap<String, Filter> filters = new LinkedHashMap<>();
        filters.put("jwt", new JWTFilter());
        shiroFilterFactoryBean.setFilters(filters);

        LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<>();

        // Swagger接口文档
        filterChainDefinitionMap.put("/v2/**", "anon");
        filterChainDefinitionMap.put("/webjars/**", "anon");
        filterChainDefinitionMap.put("/swagger-resources/**", "anon");
        filterChainDefinitionMap.put("/swagger-ui.html/**", "anon");
        filterChainDefinitionMap.put("/doc.html", "anon");
        filterChainDefinitionMap.put("/csrf", "anon");

        //不拦截注册和登录页面
        filterChainDefinitionMap.put("/login","anon");
        filterChainDefinitionMap.put("/register","anon");

//        filterChainDefinitionMap.put("/**", "jwt");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        return shiroFilterFactoryBean;
    }

    /**
     *  多个realm做认证时用的是Authenticator中的realm，但做授权用的是SecurityManager中的realms
     *  因此只将realms配置给Authenticator时会发生授权异常；
     *  所以将reaml配置给SecurityManager，如果AuthenticatingSecurityManager中的authenticator
     *  是ModularRealmAuthenticator的话会把realms配置给Authenticator
     * @return
     */
    @Bean
    public DefaultWebSecurityManager getDefaultWebSecurityManager(DBRealm dBRealm, JWTRealm jwtRealm,Authenticator authenticator){
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        defaultWebSecurityManager.setAuthenticator(authenticator);

        // 关闭Shiro自带的session
//        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
//        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
//        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
//        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
//        defaultWebSecurityManager.setSubjectDAO(subjectDAO);

        //一般来说非登录请求多于登录请求，所以把JWTRealm放在前面
        defaultWebSecurityManager.setRealms(Arrays.asList(jwtRealm,dBRealm));
        return defaultWebSecurityManager;
    }


    /**
     * 设置多realm时的策略
     * @return
     */
    @Bean
    public Authenticator getAuthenticator(){
        ModularRealmAuthenticator authenticator = new ModularRealmAuthenticator();
        authenticator.setAuthenticationStrategy(new FirstSuccessfulStrategy());
        return authenticator;
    }

    @Bean
    public DBRealm getDBRealm(HashedCredentialsMatcher matcher, EhCacheManager ehCacheManager){
        DBRealm realm = new DBRealm();

        //开启缓存管理
        realm.setCacheManager(ehCacheManager);
        //开启全局缓存
        realm.setCachingEnabled(true);
        //开启认证缓存
        realm.setAuthenticationCachingEnabled(true);
        realm.setAuthenticationCacheName("DBRealmAuthenticationCache");
        //开启授权缓存
        realm.setAuthorizationCachingEnabled(true);
        realm.setAuthorizationCacheName("DBRealmAuthorizationCache");

        //给realm设置密码匹配器
        realm.setCredentialsMatcher(matcher);
        return realm;
    }

    //DBRealm的密码匹配器
    @Bean
    public HashedCredentialsMatcher getHashedCredentialsMatcher(){
        //设置密码匹配器
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        //设置加密算法
        matcher.setHashAlgorithmName("MD5");
        //设置散列次数
        matcher.setHashIterations(10);

        return matcher;
    }

    @Bean
    public JWTRealm getJWTRealm(JWTCredentialsMatcher matcher, EhCacheManager ehCacheManager){
        JWTRealm realm = new JWTRealm();

        //token有过期时间，EhCacheManager没法设置缓存过期时间，所以关闭认证缓存，使用redis自定义缓存可以开启

        //开启缓存管理
        realm.setCacheManager(ehCacheManager);
        //开启全局缓存
        realm.setCachingEnabled(true);
        //开启认证缓存
//        realm.setAuthenticationCachingEnabled(true);
//        realm.setAuthenticationCacheName("JWTRealmAuthenticationCache");
        //开启授权缓存
        realm.setAuthorizationCachingEnabled(true);
        realm.setAuthorizationCacheName("JWTRealmAuthorizationCache");

        //给realm设置密码匹配器
        realm.setCredentialsMatcher(matcher);
        return realm;
    }

    @Bean
    public EhCacheManager getEhCacheManager(){
        return new EhCacheManager();
    }
}
