package com.way.mobile.controller.member;

import com.way.member.member.dto.MemberDto;
import com.way.mobile.common.patchca.RedisPatchcaStore;
import com.way.mobile.service.member.LoginService;
import com.way.common.exception.DataValidateException;
import com.way.common.log.WayLogger;
import com.way.common.result.ServiceResult;
import com.way.common.util.IpUtil;
import com.way.common.util.ResponseUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @ClassName: LoginController
 * @Description: 登陆Controller
 * @author: xinpei.xu
 * @date: 2017/08/17 22:49
 *
 */
@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    /**
     * @Title: getCaptchaImage
     * @Description: 生成图片验证码
     * @return: void
     */
    @RequestMapping(value = "/getCaptchaImage", method = RequestMethod.GET)
    public void getCaptchaImage(HttpServletRequest request, HttpServletResponse response) {
        try {
            String deviceNo = request.getHeader("deviceNo");
            if (StringUtils.isBlank(deviceNo)) {
                deviceNo = request.getParameter("uniqueId");
            }
            WayLogger.info("生成图片验证码：/getCaptchaImage.htm" + ",设备号deviceNo：" + deviceNo);
            if (StringUtils.isBlank(deviceNo) || "null".equals(deviceNo)) {
                ResponseUtils.toJson(response, ServiceResult.ERROR_CODE, "设备号不能为空");
                return;
            }
            RedisPatchcaStore.getImgCode(request, response, deviceNo);
        } catch (Exception e) {
            WayLogger.error(e, "图片验证码生成失败");
            ResponseUtils.toJson(response, ServiceResult.ERROR_CODE, "网络异常");
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<MemberDto> login(HttpServletRequest request, @ModelAttribute MemberDto memberDto) {
        ServiceResult<MemberDto> serviceResult = ServiceResult.newSuccess();
        try {
            // 设备号
            memberDto.setDeviceNo(request.getHeader("deviceNo"));
            // ip地址
            memberDto.setSIp(IpUtil.getIpAddr(request));
            // 手机号
            String mobile = memberDto.getPhone();
            // 密码
            String password = memberDto.getPassword();
            if (StringUtils.isEmpty(mobile)) { // 手机号为空
                serviceResult.setCode(ServiceResult.ERROR_CODE);
                serviceResult.setMessage("请输入手机号");
                return serviceResult;
            }
            if (StringUtils.isEmpty(password)) { // 密码为空
                serviceResult.setCode(ServiceResult.ERROR_CODE);
                serviceResult.setMessage("请输入密码");
                return serviceResult;
            }
            /** 调用户中心登录接口验证用户信息， 返回用户信息 */
            serviceResult = loginService.login(memberDto);
        } catch (DataValidateException e) {
            serviceResult.setCode(e.getErrCode());
            serviceResult.setMessage(e.getMessage());
        } catch (Exception e) {
            memberDto.setPassword(StringUtils.EMPTY);
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "登录失败," + "请求参数：" + memberDto);
        } finally {
            memberDto.setPassword(StringUtils.EMPTY);
            WayLogger.access("用户登录：/login.htm,参数：" + memberDto);
        }
        return serviceResult;
    }

    /**
     * @Title: logout
     * @Description: 退出应用
     * @return: String
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<String> logout(HttpServletRequest request, @ModelAttribute MemberDto memberDto) {
        ServiceResult<String> serviceResult = ServiceResult.newSuccess();
        try {
            String token = memberDto.getToken();
            /** 调用户中心退出应用接口*/
            loginService.logout(token);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "退出应用失败," + "请求参数：" + memberDto);
        } finally {
            WayLogger.access("退出应用：/logout.do,参数：" + memberDto);
        }
        return serviceResult;
    }

}
