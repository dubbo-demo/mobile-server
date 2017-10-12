package com.way.mobile.common.po;

import java.io.Serializable;

/**
 * @ClassName: LoginTokenInfo
 * @Description: 用户token信息
 * @author: xinpei.xu
 * @date: 2017/08/17 19:34
 */
public class LoginTokenInfo implements Serializable {

	private static final long serialVersionUID = 8210387796860813458L;

	private String phoneNo;
	/** token状态（0：正常登录，1：在另一台设备上登录） */
	private int status = 0;

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
