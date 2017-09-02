package com.way.mobile.po;

import java.io.Serializable;

public class MobileClient implements Serializable {

    private String version; //版本号
    private String client;  //终端（iPhone, iPad, Android）
    private String deviceNo;    //设备唯一标识符
    private String channel; //渠道
    private String userToken;//用户token
    private String userId;     //用户唯一ID标识符
    private String build; //app版本号序号

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }
}
