package com.way.mobile.controller.rewardScore;

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
 * 功能描述：
 *
 * @Author：xinpei.xu
 * @Date：2017年12月06日 17:13
 */
@Controller
public class RewardScoreController {

    @Autowired
    private MemberService memberService;

    /**
     * 查看积分明细
     * @param request
     * @param pageNumber
     * @return
     */
    @RequestMapping(value = "/getRewardScoreDetail", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> getRewardScoreDetail(HttpServletRequest request, Integer pageNumber){
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
            // 查看积分明细
            serviceResult = memberService.getRewardScoreDetail(phoneNo, pageNumber);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "查看积分明细失败," + "请求参数：phoneNo：" + phoneNo + "pageNumber：" + pageNumber);
        } finally {
            WayLogger.access("查看积分明细：/getRewardScoreDetail.do,参数：phoneNo：" + phoneNo + "pageNumber：" + pageNumber);
        }
        return serviceResult;
    }

    /**
     * 积分购买会员
     * @param request
     * @param validityDurationType
     * @return
     */
    @RequestMapping(value = "/buyMemberByRewardScore", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> buyMemberByRewardScore(HttpServletRequest request, String validityDurationType){
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
            // 积分购买会员
            serviceResult = memberService.buyMemberByRewardScore(phoneNo, validityDurationType);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "积分购买会员失败," + "请求参数：phoneNo：" + phoneNo + "validityDurationType：" + validityDurationType);
        } finally {
            WayLogger.access("积分购买会员：/buyMemberByRewardScore.do,参数：phoneNo：" + phoneNo + "validityDurationType：" + validityDurationType);
        }
        return serviceResult;
    }

    /**
     * 积分购买增值服务
     * @param request
     * @param validityDurationType
     * @return
     */
    @RequestMapping(value = "/buyValueAddedServiceByRewardScore", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> buyValueAddedServiceByRewardScore(HttpServletRequest request, String validityDurationType){
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
            // 积分购买增值服务
            serviceResult = memberService.buyValueAddedServiceByRewardScore(phoneNo, validityDurationType);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "积分购买增值服务失败," + "请求参数：phoneNo：" + phoneNo + "validityDurationType：" + validityDurationType);
        } finally {
            WayLogger.access("积分购买增值服务：/buyValueAddedServiceByRewardScore.do,参数：phoneNo：" + phoneNo + "validityDurationType：" + validityDurationType);
        }
        return serviceResult;
    }

    /**
     * 积分转增
     * @param request
     * @param rewardScore
     * @param friendPhoneNo
     * @return
     */
    @RequestMapping(value = "/transferRewardScoreToFriend", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> transferRewardScoreToFriend(HttpServletRequest request, Integer rewardScore, String friendPhoneNo){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 校验token
            if (null == rewardScore || StringUtils.isBlank(friendPhoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 积分转增
            serviceResult = memberService.transferRewardScoreToFriend(phoneNo, rewardScore, friendPhoneNo);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "积分转增失败," + "请求参数：phoneNo：" + phoneNo + "rewardScore：" + rewardScore+ "friendPhoneNo：" + friendPhoneNo);
        } finally {
            WayLogger.access("积分转增：/transferRewardScoreToFriend.do,参数：phoneNo：" + phoneNo + "rewardScore：" + rewardScore + "friendPhoneNo：" + friendPhoneNo);
        }
        return serviceResult;
    }
}
