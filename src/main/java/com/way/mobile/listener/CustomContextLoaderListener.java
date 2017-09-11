package com.way.mobile.listener;

import org.springframework.web.context.ContextLoaderListener;

/**
 * 功能描述：
 *
 * @ClassName CustomContextLoaderListener
 * @Author：xinpei.xu
 * @Date：2017/08/17 10:35
 */
public class CustomContextLoaderListener extends ContextLoaderListener {
    static{
        //设置dubbo使用slf4j来记录日志
        System.setProperty("dubbo.application.logger","slf4j");
    }
}