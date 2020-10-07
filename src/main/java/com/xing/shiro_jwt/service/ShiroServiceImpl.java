package com.xing.shiro_jwt.service;

import com.xing.shiro_jwt.dao.UserMapper;
import com.xing.shiro_jwt.shiro.JWTUtils;
import com.xing.shiro_jwt.vo.ConstantField;
import com.xing.shiro_jwt.vo.JsonResponse;
import com.xing.shiro_jwt.vo.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Collection;

@Service
public class ShiroServiceImpl implements ShiroService{

    @Resource
    UserMapper userMapper;

    @Autowired
    com.xing.shiro_jwt.shiro.DBRealm DBRealm;

    @Override
    public JsonResponse login(String id, String password) {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()){
            try{
                UsernamePasswordToken token = new UsernamePasswordToken(id, password);
                subject.login(token);
            }catch (UnknownAccountException e){
                return JsonResponse.invalidParam("用户名错误");
            }catch (IncorrectCredentialsException e){
                return JsonResponse.invalidParam("密码错误");
            }catch (LockedAccountException lae) {
                return JsonResponse.invalidParam("账号已锁定");
            } catch (ExcessiveAttemptsException eae) {
                return JsonResponse.invalidParam("用户名或密码错误次数过多");
            } catch (AuthenticationException ae) {
                return JsonResponse.invalidParam("用户名或密码错误");
            }
            if (subject.isAuthenticated()){
                User user = getUserById((String) subject.getPrincipal());
                String token = JWTUtils.sign(user);
                JsonResponse response = JsonResponse.success();
                response.put("token", token);
                return response;
            }else {
                return JsonResponse.unknownError("登陆失败");
            }
        }else {
            return JsonResponse.repeatLogin((String)subject.getPrincipal());
        }
    }


    //注册成功后不能自动登录，因为管理员创建管理员账户时，自动登录会导致重复登陆
    @Transactional
    @Override
    public JsonResponse register(User user){
        String salt = JWTUtils.getSalt(8);
        Md5Hash md5Hash = new Md5Hash(user.getPassword(),salt,10);
        user.setPassword(md5Hash.toHex());
        user.setSalt(salt);
        int res = userMapper.insert(user);
        if (res == 0){
            return JsonResponse.unknownError();
        }

        return JsonResponse.success();
    }

    /**
     * 检查学号是否已经存在
     * @param id 学号
     * @return 0 表示不存在
     */
    @Override
    public int checkExist(String id) {
        return userMapper.checkExist(id);
    }

    @Override
    public User getUserById(String id) {
        return userMapper.getUserById(id);
    }


    //修改改密码后在另一个浏览器还可以用旧密码登录
    //因为修改密码后缓存中会保存原密码，只有注销当前账户才会清空旧密码的缓存，因此手动清空缓存
    @Transactional
    @Override
    public JsonResponse changePassowrd(String oldPassword, String password){
        Subject subject = SecurityUtils.getSubject();
        String id = (String) subject.getPrincipal();
        User user = getUserById(id);
        Md5Hash md5Hash = new Md5Hash(oldPassword, user.getSalt(),10);

        if (!user.getPassword().equals(md5Hash.toHex())){
            return JsonResponse.invalidParam("旧密码不对劲！");
        }
        String salt = JWTUtils.getSalt(8);
        Md5Hash newMd5Hash = new Md5Hash(password, salt, 10);
        user.setPassword(newMd5Hash.toHex());
        user.setSalt(salt);

        //手动清空缓存  如果有更好的获取缓存管理器的方法请告诉我
        Cache<Object, AuthenticationInfo> authenticationCache = DBRealm.getAuthenticationCache();
        authenticationCache.remove(id);

        userMapper.update(user);

        return JsonResponse.success();
    }

    /**
     * 好多好多坑
     * 1，未登录时 AuthorizationInfo 和 subject.getPrincipal()都会报空指针异常，要提前判断登陆状态
     * 2，权限缓存Cache<Object, AuthorizationInfo> authorizationCache的key 是 subject.getPrincipals()
     *      subject.getPrincipal() 和  （String）subject.getPrincipal()都获取不到值
     * 3，bean被静态工具类注入时为null ,解决办法见JsonResponse
     * @return
     */
    @Override
    public String getRole() {
        Subject subject = SecurityUtils.getSubject();

        //未登录时返回""
        if (!subject.isAuthenticated()){
            return "";
        }

        Cache<Object, AuthorizationInfo> authorizationCache = DBRealm.getAuthorizationCache();
        //第一次调用的时候缓存中没有info
        AuthorizationInfo info = authorizationCache.get(subject.getPrincipals());
        if (info != null){
            Collection<String> roles = info.getRoles();
            if (roles.size() != 0){
                String role = roles.iterator().next();
                //系统中用户都是单角色
                return role;
            }
        }

        //如果缓存中没有就去数据库中取
        String id = (String)subject.getPrincipal();
        return getUserById(id).getRole();
    }

    @Override
    public Integer checkAdmin() {
        return userMapper.checkAdmin();
    }

    @Override
    public JsonResponse delete(String id) {
        if (StringUtils.isEmpty(id)){
            return JsonResponse.invalidParam();
        }
        User user = getUserById(id);
        if (user == null){
            return JsonResponse.invalidParam("用户" + id +"不存在！");
        }
        if (ConstantField.ROLE_ADMIN.equals(user.getRole())){
            return JsonResponse.noAuthority("去数据库删管理员账号吧！");
        }
        userMapper.delete(id);
        return JsonResponse.success();
    }


}
