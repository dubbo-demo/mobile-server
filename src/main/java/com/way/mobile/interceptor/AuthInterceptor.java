package com.way.mobile.interceptor;


import com.way.common.log.WayLogger;
import com.way.common.util.WebUtils;
import com.way.mobile.permission.AuthPermission;
import com.way.mobile.permission.AuthorityType;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 功能描述：用户身份鉴权拦截器
 *
 * @ClassName AuthInterceptor
 * @Author：xinpei.xu
 * @Date：2017/07/23 20:47
 */
public class AuthInterceptor extends HandlerInterceptorAdapter {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (handler.getClass().isAssignableFrom(HandlerMethod.class)) {
            AuthPermission authPermission = null;

            HandlerMethod hm = (HandlerMethod) handler;
            Class<?> clazz = hm.getBeanType();
            Method m = hm.getMethod();

            //是否需要验证当前请求URL
            boolean isPermissioin = false;

            boolean isClzAnnotation = clazz.isAnnotationPresent(AuthPermission.class);
            boolean isMethondAnnotation = m.isAnnotationPresent(AuthPermission.class);
            //如果方法和类声明中同时存在这个注解，那么方法中的会覆盖类中的设定。
            if (isMethondAnnotation) {
                authPermission = m.getAnnotation(AuthPermission.class);
            } else if (isClzAnnotation) {
                authPermission = clazz.getAnnotation(AuthPermission.class);
            }
            //获取链接完成请求地址 比如 http://localhost:8082/demo/ftl.htm 会得到 /demo/ftl.htm地址。
            String requestUrl = WebUtils.getPathWithinApplication(request);
            WayLogger.debug("拦截到了mvc方法:" + m + "方法，当前请求地址是" + requestUrl);

            //没有获得注解  及不需要权限-- 则直接运行
            if (null != authPermission) {
                //如果配置了 注解的 是否需要验证，不需要的话 直接通过
                isPermissioin = AuthorityType.YES.equals(authPermission.authType());
            } else {
                isPermissioin = true;
            }

            //先测试改成false 因为开发同事还没有做菜单地址的录入
            isPermissioin = false;

            if (!isPermissioin) {//不需要验证请求
                //有执行方法或权限不拦截
                return true;
            }


            //判断请求URL是否在用户登录后的session保存的权限列表中
            /*if (subject.isAuthenticated()) {// 本处有个特殊处理：本过滤器在shiro过滤器后面执行，shiro保证登录正常即可。
                if (subject.isPermitted(requestUrl)) {
                    return true;
                } else {
                    //跑出无权限异常
                    throw new ForbiddenException("对不起，你无权限操作本功能");
                }
            }*/
            return true;
        }
        return false;
    }

}