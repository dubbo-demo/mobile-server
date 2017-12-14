package com.way.mobile.controller.recharge;

import com.way.common.log.WayLogger;
import com.way.common.result.ServiceResult;
import com.way.mobile.service.member.MemberService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 功能描述：充值Controller
 *
 * @Author：xinpei.xu
 */
@Controller
public class RechargeController {

    @Autowired
    private MemberService memberService;

    /**
     * 查看充值记录
     * @param request
     * @param pageNumber
     * @return
     */
    @RequestMapping(value = "/getRechargeInfo", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> getRechargeInfo(HttpServletRequest request, Integer pageNumber){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (null == pageNumber) { // 第几页为空
                pageNumber = 1;
            }
            // 查看充值记录
            serviceResult = memberService.getRechargeInfo(phoneNo, pageNumber);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "查看充值记录失败," + "请求参数：phoneNo：" + phoneNo + "pageNumber：" + pageNumber);
        } finally {
            WayLogger.access("查看充值记录：/getRechargeInfo.do,参数：phoneNo：" + phoneNo + "pageNumber：" + pageNumber);
        }
        return serviceResult;
    }

    /**
     * APP获取购买订单号
     * @param request
     * @param type
     * @param validityDurationType
     * @return
     */
    @RequestMapping(value = "/getOrderNumber", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> getOrderNumber(HttpServletRequest request, String type, String validityDurationType){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 校验token
            if (StringUtils.isBlank(type)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 校验token
            if ("0".equals(type) && StringUtils.isBlank(validityDurationType)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // APP获取购买订单号
            serviceResult = memberService.getOrderNumber(phoneNo, type, validityDurationType);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "APP获取购买订单号失败," + "请求参数：phoneNo：" + phoneNo + "type：" + type + "validityDurationType：" + validityDurationType);
        } finally {
            WayLogger.access("APP获取购买订单号：/getOrderNumber.do,参数：phoneNo：" + phoneNo + "type：" + type + "validityDurationType：" + validityDurationType);
        }
        return serviceResult;
    }

    /**
     * 充值购买会员
     * @param request
     * @param validityDurationType
     * @return
     */
    @RequestMapping(value = "/buyMemberByRecharge", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> buyMemberByRecharge(HttpServletRequest request, String validityDurationType){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 校验token
            if (StringUtils.isBlank(validityDurationType)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 充值购买会员
            serviceResult = memberService.buyMemberByRecharge(phoneNo, validityDurationType);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "充值购买会员失败," + "请求参数：phoneNo：" + phoneNo + "validityDurationType：" + validityDurationType);
        } finally {
            WayLogger.access("充值购买会员：/buyMemberByRecharge.do,参数：phoneNo：" + phoneNo + "validityDurationType：" + validityDurationType);
        }
        return serviceResult;
    }

}
