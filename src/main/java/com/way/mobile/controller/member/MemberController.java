package com.way.mobile.controller.member;

import com.way.common.log.WayLogger;
import com.way.common.result.ServiceResult;
import com.way.common.util.Validater;
import com.way.member.friend.dto.FriendsInfoDto;
import com.way.member.member.dto.MemberDto;
import com.way.mobile.service.member.MemberService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName: MemberInfoController
 * @Description: 用户信息Controller
 * @author: xinpei.xu
 * @date: 2017/08/21 19:28
 *
 */
@Controller
public class MemberController {

    @Autowired
    private MemberService memberService;

    /**
     * 校验邀请人手机号是否存在
     */
    @RequestMapping(value = "/checkPhone", method = RequestMethod.POST)
    public ServiceResult<String> checkPhone(@ModelAttribute String phoneNo){
        ServiceResult<String> serviceResult = ServiceResult.newSuccess();
        try {
            if (StringUtils.isBlank(phoneNo)) { // 手机号为空
                serviceResult.setCode(ServiceResult.ERROR_CODE);
                serviceResult.setMessage("请输入邀请人手机号");
                return serviceResult;
            }
            // 校验手机号格式是否正确
            if (!Validater.isMobileNew(phoneNo)) {
                serviceResult.setCode(ServiceResult.ERROR_CODE);
                serviceResult.setMessage("邀请人手机号不正确");
                return serviceResult;
            }
            // 校验邀请人手机号是否存在
            serviceResult = memberService.checkPhone(phoneNo);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "校验邀请人手机号是否存在失败," + "请求参数：" + phoneNo);
        } finally {
            WayLogger.access("校验邀请人手机号是否存在：/checkPhone.do,参数：" + phoneNo);
        }
        return serviceResult;
    }

    /**
     * 根据手机号搜索用户
     * @param request
     * @param friendPhoneNo
     * @return
     */
    @RequestMapping(value = "/searchUserByPhoneNo", method = RequestMethod.POST)
    public ServiceResult<MemberDto> searchUserByPhoneNo(HttpServletRequest request, @ModelAttribute String friendPhoneNo){
        ServiceResult<MemberDto> serviceResult = ServiceResult.newSuccess();
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(friendPhoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 根据手机号搜索用户
            serviceResult = memberService.searchUserByPhoneNo(friendPhoneNo);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "根据手机号搜索用户失败," + "请求参数：phoneNo：" + phoneNo + "，friendPhoneNo：" + friendPhoneNo);
        } finally {
            WayLogger.access("根据手机号搜索用户：/searchUserByPhoneNo.do,参数：phoneNo：" + phoneNo + "，friendPhoneNo：" + friendPhoneNo);
        }
        return serviceResult;
    }
}
