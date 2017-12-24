package com.way.mobile.controller.member;

import com.way.common.log.WayLogger;
import com.way.common.result.ServiceResult;
import com.way.common.util.Validater;
import com.way.member.member.dto.MemberDto;
import com.way.mobile.service.member.MemberService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    @ResponseBody
    public ServiceResult<String> checkPhone(HttpServletRequest request, String friendPhoneNo){
        ServiceResult<String> serviceResult = ServiceResult.newSuccess();
        try {
            if (StringUtils.isBlank(friendPhoneNo)) { // 手机号为空
                serviceResult.setCode(ServiceResult.ERROR_CODE);
                serviceResult.setMessage("请输入邀请人手机号");
                return serviceResult;
            }
            // 校验手机号格式是否正确
            if (!Validater.isMobileNew(friendPhoneNo)) {
                serviceResult.setCode(ServiceResult.ERROR_CODE);
                serviceResult.setMessage("邀请人手机号不正确");
                return serviceResult;
            }
            // 校验邀请人手机号是否存在
            serviceResult = memberService.checkPhone(friendPhoneNo);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "校验邀请人手机号是否存在失败," + "请求参数：friendPhoneNo：" + friendPhoneNo);
        } finally {
            WayLogger.access("校验邀请人手机号是否存在：/checkPhone.do,参数：friendPhoneNo：" + friendPhoneNo);
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
    @ResponseBody
    public ServiceResult<MemberDto> searchUserByPhoneNo(HttpServletRequest request, String friendPhoneNo){
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
            serviceResult = memberService.searchUserByPhoneNo(phoneNo, friendPhoneNo);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "根据手机号搜索用户失败," + "请求参数：phoneNo：" + phoneNo + "，friendPhoneNo：" + friendPhoneNo);
        } finally {
            WayLogger.access("根据手机号搜索用户：/searchUserByPhoneNo.do,参数：phoneNo：" + phoneNo + "，friendPhoneNo：" + friendPhoneNo);
        }
        return serviceResult;
    }

    /**
     * 查看个人信息
     * @param request
     * @return
     */
    @RequestMapping(value = "/getMemberInfo", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<MemberDto> getMemberInfo(HttpServletRequest request){
        ServiceResult<MemberDto> serviceResult = ServiceResult.newSuccess();
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 查看个人信息
            serviceResult = memberService.getMemberInfo(phoneNo);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "查看个人信息失败," + "请求参数：phoneNo：" + phoneNo);
        } finally {
            WayLogger.access("查看个人信息：/getMemberInfo.do,参数：phoneNo：" + phoneNo);
        }
        return serviceResult;
    }

    /**
     * 修改个人信息
     * @param request
     * @param dto
     * @return
     */
    @RequestMapping(value = "/modifyMemberInfo", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> modifyMemberInfo(HttpServletRequest request, @ModelAttribute MemberDto dto){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(dto.getHeadPic()) && StringUtils.isBlank(dto.getNickName())
                    && StringUtils.isBlank(dto.getAge()) && StringUtils.isBlank(dto.getGender())) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 修改个人信息
            dto.setPhoneNo(phoneNo);
            serviceResult = memberService.modifyMemberInfo(dto);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "修改个人信息失败," + "请求参数：phoneNo：" + phoneNo + ",MemberDto：" + dto);
        } finally {
            WayLogger.access("修改个人信息：/modifyMemberInfo.do,参数：phoneNo：" + phoneNo + ",MemberDto：" + dto);
        }
        return serviceResult;
    }

    /**
     * 查看用户增值服务时间
     * @param request
     * @param type
     * @return
     */
    @RequestMapping(value = "/getMemberValueAddedTime", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> getMemberValueAddedTime(HttpServletRequest request, String type){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 校验type
            if (!"1".equals(type) && !"2".equals(type) ) {
                return ServiceResult.newFailure("必传参数有误");
            }
            // 查看用户增值服务时间
            serviceResult = memberService.getMemberValueAddedTime(phoneNo, type);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "查看用户增值服务时间失败," + "请求参数：phoneNo：" + phoneNo + "type：" + type);
        } finally {
            WayLogger.access("查看用户增值服务时间：/getMemberValueAddedTime.do,参数：phoneNo：" + phoneNo + "type：" + type);
        }
        return serviceResult;
    }
}
