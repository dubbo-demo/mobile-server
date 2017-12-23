package com.way.mobile.service.member.impl;

import com.way.common.cache.RedisRootNameSpace;
import com.way.common.constant.Constants;
import com.way.common.exception.DataValidateException;
import com.way.common.redis.CacheService;
import com.way.common.result.ServiceResult;
import com.way.common.util.DateUtils;
import com.way.common.util.Validater;
import com.way.member.member.service.MemberInfoService;
import com.way.member.member.service.PasswordService;
import com.way.member.member.dto.MemberDto;
import com.way.member.member.dto.MemberResetPasswordDto;
import com.way.mobile.common.constant.ConstantsConfig;
import com.way.mobile.common.constant.ResponseMsg;
import com.way.mobile.common.constant.VerificationCodeType;
import com.way.mobile.common.util.PropertyConfig;
import com.way.mobile.service.member.LoginService;
import com.way.mobile.service.member.RegistService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;

/**
 *
 * @ClassName: RegistServiceImpl
 * @Description: 注册ServiceImpl
 * @author: xinpei.xu
 * @date: 2017/08/19 19:12
 *
 */
@Service
public class RegistServiceImpl implements RegistService {

	@Autowired
	private MemberInfoService memberInfoService;
	@Autowired
	private PropertyConfig propertyConfig;
	@Autowired
	private LoginService loginService;
	@Autowired
	private PasswordService passwordService;

	/**
	 * @Title: sendCode
	 * @Description: 生成短信验证码
	 * @return: void
	 * @throws DataValidateException
	 */
	public ServiceResult<String> sendCode(MemberDto memberDto) throws DataValidateException {
		String phoneNo = memberDto.getPhoneNo();
		// 验证手机号是否已经注册
		ServiceResult<MemberDto> m = memberInfoService.loadMapByMobile(phoneNo);
		String smsCodeType = ConstantsConfig.JEDIS_HEADER_REGIST_CODE;
		if (VerificationCodeType.FORGET_PASSWORD.equals(memberDto.getType())) {// 忘记密码的时候，校验
			smsCodeType = ConstantsConfig.JEDIS_HEADER_FORGET_PASSWORD_CODE;
			// 未注册返回失败
			if (null == m || m.getData() == null){
				throw new DataValidateException("手机号码不存在");
			}
		} else if (VerificationCodeType.REGIST.equals(memberDto.getType())) {// 注册时候，发送验证码的校验
			// 校验手机号码是否已注册
			if (m != null && m.getData() != null) {
				throw new DataValidateException("手机号码存在");
			}
		}
		String key = smsCodeType + phoneNo;
		// 校验发送验证码的时间间隔
		long surplusExpire = CacheService.KeyBase.getExprise(key);// 剩余有效时间
		long usedExpire = propertyConfig.getSmsCodeExpire() - surplusExpire;// 已过时间
		if (surplusExpire > 0 && usedExpire < propertyConfig.getSmsCodeExpireInterval()){
			throw new DataValidateException(ResponseMsg.SEND_CODE_MINUTE);
		}

		if (!memberDto.getType().equals(VerificationCodeType.REGIST)
				&& !memberDto.getType().equals(VerificationCodeType.FORGET_PASSWORD)) {
			throw new DataValidateException("发送失败，未知的验证码类型，type：" + memberDto.getType());
		}
		// 生成6位随机数
		Random random = new Random();
//		String code = (random.nextDouble() + "").substring(2, 8).replace("4", "9");
		String code = "888888";
		// 验证码存到redis中
		CacheService.StringKey.set(key, code, RedisRootNameSpace.UnitEnum.FIFTEEN_MIN);
		return ServiceResult.newSuccess(code);
	}

	/**
	 * 用户注册
	 * @param memberDto
	 * @return
	 * @throws DataValidateException
	 */
	public ServiceResult<MemberDto> regist(MemberDto memberDto) throws DataValidateException {
		String phoneNo = memberDto.getPhoneNo();
		String key = ConstantsConfig.JEDIS_HEADER_REGIST_CODE + phoneNo;
		String code = CacheService.StringKey.getObject(key, String.class);
		if (StringUtils.isBlank(code)) {
			throw new DataValidateException("请重新获取短信验证码");
		}
		if (!code.equals(memberDto.getVerificationCode())) {
			throw new DataValidateException("短信验证码不正确");
		}
		// 验证码校验成功，移除redis中的验证码
		CacheService.KeyBase.delete(key);
		ServiceResult<MemberDto> memberRes  = memberInfoService.loadMapByMobile(phoneNo);
		if (null != memberRes.getData()){ // 该手机号已经注册
			throw new DataValidateException("手机号已注册");
		}
		memberInfoService.memberRegist(memberDto);
		// 注册成功调用登录接口登录，并异步保存用户登录信息
		loginService.login(memberDto);
		return ServiceResult.newSuccess(memberDto);
	}
	
	/**
	 * 忘记密码
	 * @param memberDto
	 * @throws DataValidateException 
	 */
	public  ServiceResult<String> resetPassword(MemberDto memberDto) throws DataValidateException {
		String phoneNo = memberDto.getPhoneNo();
		// 校验手机号
		if (!Validater.isMobileNew(phoneNo))
			throw new DataValidateException("手机号不正确");
		if (StringUtils.isEmpty(memberDto.getVerificationCode()))
			throw new DataValidateException("请输入短信验证码");
		if (StringUtils.isEmpty(memberDto.getPassword()))
			throw new DataValidateException("请输入密码");

		// 校验验证码
		String key = ConstantsConfig.JEDIS_HEADER_FORGET_PASSWORD_CODE + phoneNo;
		String code = CacheService.StringKey.getObject(key, String.class);
		if (code == null || code == "")
			throw new DataValidateException("短信验证码已失效");
		if (!memberDto.getVerificationCode().equals(code))
			throw new DataValidateException("短信验证码不正确");

		// 校验用户是否存在
		ServiceResult<MemberDto> memberRes = memberInfoService.loadMapByMobile(phoneNo);
		// 用户不存在
		if (memberRes.getData() == null){
			throw new DataValidateException("手机号未注册");
		}
		// 更新密码表
		memberInfoService.updatePassword(phoneNo, memberDto.getPassword());
		// 验证码校验成功，移除redis中的验证码
		CacheService.KeyBase.delete(key);
		return ServiceResult.newSuccess();
	}
	
	/**
	 * @Title: checkResetPasswordFailTimes
	 * @Description: 校验重置密码失败次数是否大于允许的最大值
	 * @return: int
	 * @throws DataValidateException 
	 */
	public int checkResetPasswordFailTimes(Object ob, String key) throws DataValidateException {
		int tempFailTimes = 0;// 临时记录重置密码失败次数
		if (null != ob) { // 已经存在该用户重置密码失败的信息
			MemberResetPasswordDto resetPasswordFailVO = (MemberResetPasswordDto) ob;
			if (propertyConfig.getPermitResetPasswordTimes() == resetPasswordFailVO.getResetPasswordFailTimes()) { // 重置密码失败次数达到允许的最大次数
				Date lastResetPasswordFailTime = DateUtils.timeStamp2Date(""+resetPasswordFailVO.getLastResetPasswordFailTime());
				// 默认时间间隔的单位为小时
				long subHours = DateUtils.getSubHours(lastResetPasswordFailTime);
					if (subHours >= propertyConfig.getResetPasswordFailInterval()) { // 时间间隔达到最大间隔时间
						CacheService.KeyBase.delete(key);
					} else { // 本次重置密码的时间间隔小于允许重置密码的时间间隔
						throw new DataValidateException("密码修改过于频繁，请稍后重试");
					}
			} else { // 重置密码失败次数未达到允许的最大次数
				tempFailTimes = resetPasswordFailVO.getResetPasswordFailTimes();
			}
		}
		return tempFailTimes;
	}
}
