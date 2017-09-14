package com.way.mobile.permission;

/**
 * 功能描述：注解到 controller的方法上  标识当前请求是否需要验证用户是否登录
 *
 * @ClassName AuthorityType
 * @Author：xinpei.xu
 * @Date：2017/07/20 20:56
 */
public enum AuthorityType {
    //用户必须要验证 YES 不要验证  no：不需要验证
    YES,NO;
}