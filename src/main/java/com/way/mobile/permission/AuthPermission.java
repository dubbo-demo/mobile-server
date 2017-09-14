package com.way.mobile.permission;

import java.lang.annotation.*;

/**
 * 功能描述：服务鉴权方式 ：注解可以加在方法
 *
 * @ClassName AuthPermission
 * @Author：xinpei.xu
 * @Date：2017/07/20 20:49
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AuthPermission {
    /**
     * 是否需要验证 ：yes：需要 no：不需要
     * @return
     */
    AuthorityType authType() default AuthorityType.YES;
}