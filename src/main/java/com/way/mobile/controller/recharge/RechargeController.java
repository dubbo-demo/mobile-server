package com.way.mobile.controller.recharge;

import com.alibaba.fastjson.JSON;
import com.way.base.beeCloud.dto.BeeCloudDto;
import com.way.base.beeCloud.dto.BeeCloudMessageDetailDto;
import com.way.base.beeCloud.service.BeeCloudMessageDetailService;
import com.way.common.log.WayLogger;
import com.way.common.result.ServiceResult;
import com.way.common.util.CommonUtils;
import com.way.common.util.ResponseUtils;
import com.way.common.util.WayMD5;
import com.way.mobile.service.member.AsyncPushBeeCloudMessageService;
import com.way.mobile.service.member.MemberService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Enumeration;

/**
 * 功能描述：充值Controller
 *
 * @Author：xinpei.xu
 */
@Controller
public class RechargeController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private BeeCloudMessageDetailService beeCloudMessageDetailService;

    @Autowired
    private AsyncPushBeeCloudMessageService asyncPushBeeCloudMessageService;

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
        String invitationCode = (String) request.getAttribute("invitationCode");
        try {
            // 校验token
            if (StringUtils.isBlank(invitationCode)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (null == pageNumber) { // 第几页为空
                pageNumber = 1;
            }
            // 查看充值记录
            serviceResult = memberService.getRechargeInfo(invitationCode, pageNumber);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            serviceResult.setMessage(ServiceResult.ERROR_MSG);
            WayLogger.error(e, "查看充值记录失败," + "请求参数：invitationCode：" + invitationCode + "pageNumber：" + pageNumber);
        } finally {
            WayLogger.access("查看充值记录：/getRechargeInfo.do,参数：invitationCode：" + invitationCode + "pageNumber：" + pageNumber);
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
        String invitationCode = (String) request.getAttribute("invitationCode");
        try {
            // 校验token
            if (StringUtils.isBlank(invitationCode)) {
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
            serviceResult = memberService.getOrderNumber(invitationCode, type, validityDurationType);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            serviceResult.setMessage(ServiceResult.ERROR_MSG);
            WayLogger.error(e, "APP获取购买订单号失败," + "请求参数：invitationCode：" + invitationCode + "type：" + type + "validityDurationType：" + validityDurationType);
        } finally {
            WayLogger.access("APP获取购买订单号：/getOrderNumber.do,参数：invitationCode：" + invitationCode + "type：" + type + "validityDurationType：" + validityDurationType);
        }
        return serviceResult;
    }

    /**
     * 支付回调
     */
    @RequestMapping(value = "/beeCloudCallBack", method = RequestMethod.POST)
    public void beeCloudCallBack(HttpServletRequest request, HttpServletResponse response, @RequestBody BeeCloudDto<BeeCloudMessageDetailDto> dto) {
        WayLogger.error(dto.toString());
        try {
            if(dto == null){
                WayLogger.error("入参为空");
                ResponseUtils.beeCloudResponse(response, "fail");
                return;
            }
            // 验签
            String md5 = WayMD5.encode(dto.getApp_id() + dto.getTransaction_id() + dto.getTransaction_type() + dto.getChannel_type() +
                    dto.getTransaction_fee() + dto.getMaster_secret());
            if(!md5.equals(dto.getSignature())){
                WayLogger.error("验签失败");
                ResponseUtils.beeCloudResponse(response, "fail");
                return;
            }
            BeeCloudMessageDetailDto message_detail = CommonUtils.transform(dto.getMessage_detail(), BeeCloudMessageDetailDto.class);
            if(message_detail == null){
                WayLogger.error("入参为空");
                ResponseUtils.beeCloudResponse(response, "fail");
                return;
            }
            if (StringUtils.isBlank(message_detail.getTransaction_id())) {
                WayLogger.error("微信交易号为空");
                ResponseUtils.beeCloudResponse(response, "fail");
                return;
            }
            if (StringUtils.isBlank(message_detail.getOut_trade_no())) {
                WayLogger.error("商家内部交易号为空");
                ResponseUtils.beeCloudResponse(response, "fail");
                return;
            }
            if (StringUtils.isBlank(message_detail.getTotal_fee())) {
                WayLogger.error("商品总价（单位为分）为空");
                ResponseUtils.beeCloudResponse(response, "fail");
                return;
            }
            if (StringUtils.isBlank(message_detail.getCash_fee())) {
                WayLogger.error("现金付款额为空");
                ResponseUtils.beeCloudResponse(response, "fail");
                return;
            }
            if (StringUtils.isBlank(message_detail.getAppid())) {
                WayLogger.error("买家的openid为空");
                ResponseUtils.beeCloudResponse(response, "fail");
                return;
            }
            if ("f40dfec0-1a82-47c9-83a3-847735097111".equals(message_detail.getAppid())) {
                WayLogger.error("买家的openid有误");
                ResponseUtils.beeCloudResponse(response, "fail");
                return;
            }
            // 解析入库
            // 查询BeeCloud回参记录
            BeeCloudMessageDetailDto record = beeCloudMessageDetailService.getBeeCloudMessageDetailDto(message_detail);
            if(null == record){
                message_detail.setFlag(2);
                message_detail.setCreateTime(new Date());
                message_detail.setModifyTime(new Date());
                // 保存BeeCloud回参信息
                beeCloudMessageDetailService.saveBeeCloudMessageDetailDto(message_detail);

                // 异步推送BeeCloud回参信息
                asyncPushBeeCloudMessageService.pushBeeCloudMessage(message_detail);
            }
            if(null != record && 2 == record.getFlag()){
                // 异步推送BeeCloud回参信息
                asyncPushBeeCloudMessageService.pushBeeCloudMessage(message_detail);
            }
            response.getWriter().write(JSON.toJSONString("success"));
            response.getWriter().flush();
        } catch (Exception e) {
            WayLogger.error("入库失败", "", e);
            ResponseUtils.beeCloudResponse(response, "fail");
        }
    }

}
