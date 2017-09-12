package com.way.mobile.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @ClassName: PropertyConfig
 * @Description: 配置属性工具类
 * @author xinpei.xu
 * @date 2017/08/15 20:08
 */
@Component
public class PropertyConfig {

	/** 允许最大登录失败次数 */
	@Value("${permit.login.times}")
	private int permitLoginTimes;

	/** 登录失败的间隔时间单位 */
	@Value("${login.fail.interval.unit}")
	private int loginFailIntervalUnit;

	/** 达到最大登录次数允许下次登录的时间间隔 */
	@Value("${login.fail.interval}")
	private int loginFailInterval;

	/** 验证码的有效时长 */
	@Value("${smsCode.expire}")
	private long smsCodeExpire;

	/** 验证码重新发送的时间间隔 */
	@Value("${smsCode.expire.interval}")
	private int smsCodeExpireInterval;

	/** 允许最大重置密码失败次数 */
	@Value("${permit.reset.password.times}")
	private int permitResetPasswordTimes;

	/** 达到最大重置密码次数允许下次重置密码的时间间隔 */
	@Value("${reset.password.fail.interval}")
	private int resetPasswordFailInterval;

	@Value("${mobileCenter.url}")
    private String mobileUrl;
	   
	public int getPermitLoginTimes() {
		return permitLoginTimes;
	}

	public void setPermitLoginTimes(int permitLoginTimes) {
		this.permitLoginTimes = permitLoginTimes;
	}

	public int getLoginFailIntervalUnit() {
		return loginFailIntervalUnit;
	}

	public void setLoginFailIntervalUnit(int loginFailIntervalUnit) {
		this.loginFailIntervalUnit = loginFailIntervalUnit;
	}

	public int getLoginFailInterval() {
		return loginFailInterval;
	}

	public void setLoginFailInterval(int loginFailInterval) {
		this.loginFailInterval = loginFailInterval;
	}

	public long getSmsCodeExpire() {
		return smsCodeExpire;
	}

	public void setSmsCodeExpire(long smsCodeExpire) {
		this.smsCodeExpire = smsCodeExpire;
	}

	public int getSmsCodeExpireInterval() {
		return smsCodeExpireInterval;
	}

	public void setSmsCodeExpireInterval(int smsCodeExpireInterval) {
		this.smsCodeExpireInterval = smsCodeExpireInterval;
	}

	public int getPermitResetPasswordTimes() {
		return permitResetPasswordTimes;
	}

	public void setPermitResetPasswordTimes(int permitResetPasswordTimes) {
		this.permitResetPasswordTimes = permitResetPasswordTimes;
	}

	public int getResetPasswordFailInterval() {
		return resetPasswordFailInterval;
	}

	public void setResetPasswordFailInterval(int resetPasswordFailInterval) {
		this.resetPasswordFailInterval = resetPasswordFailInterval;
	}

    public String getMobileUrl() {
        return mobileUrl;
    }

    public void setMobileUrl(String mobileUrl) {
        this.mobileUrl = mobileUrl;
    }
	
}