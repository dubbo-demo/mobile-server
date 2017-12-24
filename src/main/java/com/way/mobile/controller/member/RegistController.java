package com.way.mobile.controller.member;

import com.way.base.common.SmsTemplateEnum;
import com.way.common.exception.DataValidateException;
import com.way.common.log.WayLogger;
import com.way.common.redis.CacheService;
import com.way.common.result.ServiceResult;
import com.way.common.util.Validater;
import com.way.member.member.dto.MemberDto;
import com.way.mobile.common.constant.ConstantsConfig;
import com.way.mobile.common.patchca.RedisPatchcaStore;
import com.way.mobile.common.util.TokenJedisUtils;
import com.way.mobile.service.member.RegistService;
import com.way.base.sms.service.SmsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @ClassName: RegistController
 * @Description: 注册Controller
 * @author: xinpei.xu
 * @date: 2017/08/17 22:30
 *
 */
@Controller
public class RegistController {
	
	@Autowired
	private RegistService registService;

	@Autowired
	private SmsService smsService;
	
	/**
	 * 获得平台验证码
	 */
	@RequestMapping(value = "/getVerificationCode", method = RequestMethod.POST)
	@ResponseBody
	public ServiceResult<String> getVerificationCode(final HttpServletRequest request, @ModelAttribute MemberDto params) {
		// 设置设备号
		params.setDeviceNo(request.getHeader("deviceNo"));
		WayLogger.info("生成图片验证码：/getVerificationCode.htm" + ",参数："+ params);
		ServiceResult<String> serviceResult = ServiceResult.newSuccess();
		try {
			// 校验设备号是否为空
			if (StringUtils.isBlank(params.getDeviceNo()) || "null".equals(params.getDeviceNo())) {
				WayLogger.error("发送短信验证码........异常，deviceNo设备号不能为空");
				serviceResult.setCode(ServiceResult.ERROR_CODE);
				serviceResult.setMessage("设备号不能为空");
				return serviceResult;
			}
			// 校验图片验证码
			boolean isPass = RedisPatchcaStore.validateImgCode(params.getDeviceNo(), params.getImgCode());
			if (!isPass) {
				serviceResult.setCode(ServiceResult.ERROR_CODE);
				serviceResult.setMessage("图片验证码不正确");
				return serviceResult;
			}
			// 发生短信验证码
			ServiceResult<String> smsResult = registService.sendCode(params);
			String smsCode = smsResult.getData();
			if(StringUtils.isNotBlank(smsCode)){
				smsService.sendSms(smsCode, params.getPhoneNo(), SmsTemplateEnum.APP_REGIST_TEMPLATE);
			}else{
				serviceResult.setCode(ServiceResult.ERROR_CODE);
				serviceResult.setMessage("发送短信验证码失败");
			}
		}catch (DataValidateException e) {
			WayLogger.error(e,"发送短信验证码异常");
			serviceResult.setCode(ServiceResult.ERROR_CODE);
			serviceResult.setMessage(e.getMessage());
		}catch (Exception e) {
			WayLogger.error(e,"发送短信验证码异常");
			serviceResult.setCode(ServiceResult.ERROR_CODE);
			serviceResult.setMessage("失败");
		}
		return serviceResult;
	}
	
	/**
	 * @Title: regist
	 * @Description: 注册
	 * @return: String
	 */
	@RequestMapping(value = "/regist", method = RequestMethod.POST)
	@ResponseBody
	public ServiceResult<Object> regist(HttpServletRequest request, @ModelAttribute MemberDto memberDto) {
		ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
		try {
			// app版本号
			memberDto.setVersion(request.getHeader("version"));
			// 设备号
            memberDto.setDeviceNo(request.getHeader("deviceNo"));
			// 校验短信验证码
			if (StringUtils.isEmpty(memberDto.getVerificationCode())) {
				serviceResult.setCode(ServiceResult.ERROR_CODE);
				serviceResult.setMessage("请输入短信验证码");
				return serviceResult;
			}
			// 校验手机号
			if (Validater.isEmpty(memberDto.getPhoneNo())) {
				serviceResult.setCode(ServiceResult.ERROR_CODE);
				serviceResult.setMessage("请输入手机号");
				return serviceResult;
			}
			// 校验手机号格式是否正确
			if (!Validater.isMobileNew(memberDto.getPhoneNo())) {
				serviceResult.setCode(ServiceResult.ERROR_CODE);
				serviceResult.setMessage("手机号不正确");
				return serviceResult;
			}
			/** 调用户中心用户注册接口 */
			ServiceResult<MemberDto> result = registService.regist(memberDto);
			MemberDto dto = result.getData();
			serviceResult.setData(dto);
			if (serviceResult.getCode().equals(ServiceResult.SUCCESS_CODE)) {// 注册成功
				// 生成新token
				String newToken = TokenJedisUtils.putTokenInfoExpire(dto.getPhoneNo());
				// dto
				dto.setToken(newToken);
				serviceResult.setData(dto);
			}
		} catch (DataValidateException e) {
			serviceResult.setCode(ServiceResult.ERROR_CODE);
			serviceResult.setMessage(e.getMessage());
			WayLogger.error(e,"注册失败");
		} catch (Exception e) {
			serviceResult.setCode(ServiceResult.ERROR_CODE);
			serviceResult.setMessage("注册失败");
			WayLogger.error(e,"注册失败");
		} finally {
			memberDto.setPassword(StringUtils.EMPTY);
			WayLogger.access("注册：/regist.htm" + ",参数："+ memberDto);
		}
		return serviceResult;
	}
	
	/**
	 * @Title: resetPassword
	 * @Description: 重置密码
	 * @return: ServiceResult<Object>
	 */
	@RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
	@ResponseBody
	public ServiceResult<MemberDto> resetPassword(HttpServletRequest request, @ModelAttribute MemberDto memberDto) {
		ServiceResult<MemberDto> serviceResult = ServiceResult.newSuccess();
		try {
			// 校验参数
			if (StringUtils.isEmpty(memberDto.getPhoneNo()) || StringUtils.isEmpty(memberDto.getPassword())
					|| StringUtils.isEmpty(memberDto.getVerificationCode())) {
				return ServiceResult.newFailure("必传参数不能为空");
			}
			/// 重置密码
			serviceResult = registService.resetPassword(memberDto);
			if (serviceResult.getCode() == ServiceResult.SUCCESS_CODE) {
				// 密码修改成功，删除redis中登录失败的记录
				CacheService.KeyBase.delete(ConstantsConfig.JEDIS_HEADER_LOGIN_FAIL + memberDto.getPhoneNo());
				// 生成新token
				String newToken = TokenJedisUtils.putTokenInfoExpire(memberDto.getPhoneNo());
				// dto
				memberDto.setToken(newToken);
				serviceResult.setData(memberDto);
			}
		} catch (DataValidateException e) {
			serviceResult.setCode(ServiceResult.ERROR_CODE);
			serviceResult.setMessage(e.getMessage());
			WayLogger.error(e,"重置密码失败");
		} catch (Exception e) {
			serviceResult.setCode(ServiceResult.ERROR_CODE);
			serviceResult.setMessage("重置密码失败");
			WayLogger.error(e,"重置密码失败");
		} finally {
			WayLogger.access("重置密码：/resetPassword.do" + ",参数："+ memberDto);
		}
		return serviceResult;
	}
	
}
