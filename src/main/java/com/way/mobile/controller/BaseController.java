package com.way.mobile.controller;

/**
 * INFO: info
 * User: zhaokai
 * Date: 2016/8/26 - 9:31
 * Version: 1.0
 * History: <p>如果有修改过程，请记录</P>
 */

import com.way.mobile.po.MobileClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public abstract class BaseController {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    protected static final int DEFAULT_PAGE_SIZE = 10;


    public BaseController() {
    }

    public String getPath(HttpServletRequest request) {
        return request.getContextPath();
    }

    public String getPath(HttpServletRequest request, String url) {
        return request.getContextPath() + url;
    }


    public String getCurrentUsername() {
        return "";
    }

    protected MobileClient mobileClient = new MobileClient();

    public MobileClient getMobileClient() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if (request != null) {
            String version = request.getHeader("version"); // 版本号
            String devType = request.getHeader("client"); // 终端类型,iPhone,iPad, android
            if (devType == null) {
                String userAgent = request.getHeader("user-agent");
                if (userAgent != null && userAgent.toLowerCase().contains("android")) {
                    devType = "android";
                } else if (userAgent != null && userAgent.toLowerCase().contains("iphone")) {
                    devType = "iPhone";
                }
                devType = (devType == null) ? "android" : devType;
            }
            String deviceNo = request.getHeader("deviceNo"); // 设备唯一标识符
            String userId = request.getHeader("userId"); // 用户唯一ID标识符
            String channel = request.getHeader("channel"); // 渠道号
            String userToken = request.getHeader("userToken"); // 用户token
            String build = request.getHeader("build");

            mobileClient.setVersion(version);
            mobileClient.setDeviceNo(deviceNo);
            if (!StringUtils.isEmpty(devType)) {
                mobileClient.setClient(devType.toLowerCase());
            }
            mobileClient.setUserId(userId);
            mobileClient.setUserToken(userToken);
            mobileClient.setChannel(channel);
            mobileClient.setBuild(build);
        }
        return mobileClient;
    }

}
