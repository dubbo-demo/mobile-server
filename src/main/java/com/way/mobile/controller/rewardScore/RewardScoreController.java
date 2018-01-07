package com.way.mobile.controller.rewardScore;

import com.way.common.exception.DataValidateException;
import com.way.common.log.WayLogger;
import com.way.common.redis.CacheService;
import com.way.common.result.ServiceResult;
import com.way.member.withdrawal.dto.WithdrawalInfoDto;
import com.way.mobile.common.constant.ConstantsConfig;
import com.way.mobile.service.member.MemberService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 功能描述：积分Controller
 *
 * @Author：xinpei.xu
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
            serviceResult.setMessage(ServiceResult.ERROR_MSG);
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
            serviceResult.setMessage(ServiceResult.ERROR_MSG);
            WayLogger.error(e, "积分购买会员失败," + "请求参数：phoneNo：" + phoneNo + "validityDurationType：" + validityDurationType);
        } finally {
            WayLogger.access("积分购买会员：/buyMemberByRewardScore.do,参数：phoneNo：" + phoneNo + "validityDurationType：" + validityDurationType);
        }
        return serviceResult;
    }

    /**
     * 积分购买增值服务
     * @param request
     * @return
     */
    @RequestMapping(value = "/buyValueAddedServiceByRewardScore", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> buyValueAddedServiceByRewardScore(HttpServletRequest request, String type){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(type)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 积分购买增值服务
            serviceResult = memberService.buyValueAddedServiceByRewardScore(phoneNo, type);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            serviceResult.setMessage(ServiceResult.ERROR_MSG);
            WayLogger.error(e, "积分购买增值服务失败," + "请求参数：phoneNo：" + phoneNo + "type：" + type);
        } finally {
            WayLogger.access("积分购买增值服务：/buyValueAddedServiceByRewardScore.do,参数：phoneNo：" + phoneNo + "type：" + type);
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
    public ServiceResult<Object> transferRewardScoreToFriend(HttpServletRequest request, Double rewardScore, String friendPhoneNo, String verificationCode){
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
            String key = ConstantsConfig.JEDIS_HEADER_TRANSFE_RREWARDSCORE_CODE + phoneNo;
            String code = CacheService.StringKey.getObject(key, String.class);
            if (StringUtils.isBlank(code)) {
                throw new DataValidateException("请重新获取短信验证码");
            }
            if (!code.equals(verificationCode)) {
                throw new DataValidateException("短信验证码不正确");
            }
            // 验证码校验成功，移除redis中的验证码
            CacheService.KeyBase.delete(key);
            // 积分转增
            serviceResult = memberService.transferRewardScoreToFriend(phoneNo, rewardScore, friendPhoneNo);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            serviceResult.setMessage(ServiceResult.ERROR_MSG);
            WayLogger.error(e, "积分转增失败," + "请求参数：phoneNo：" + phoneNo + "rewardScore：" + rewardScore+ "friendPhoneNo：" + friendPhoneNo);
        } finally {
            WayLogger.access("积分转增：/transferRewardScoreToFriend.do,参数：phoneNo：" + phoneNo + "rewardScore：" + rewardScore + "friendPhoneNo：" + friendPhoneNo);
        }
        return serviceResult;
    }

    /**
     * 积分提现
     * @param request
     * @param withdrawalInfoDto
     * @return
     */
    @RequestMapping(value = "/withdrawalRewardScore", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> withdrawalRewardScore(HttpServletRequest request, WithdrawalInfoDto withdrawalInfoDto, String verificationCode){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 校验token
            if (StringUtils.isBlank(withdrawalInfoDto.getBankName()) || StringUtils.isBlank(withdrawalInfoDto.getBankNumber()) ||
                    StringUtils.isBlank(withdrawalInfoDto.getName()) || null == withdrawalInfoDto.getRewardScore()) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            String key = ConstantsConfig.JEDIS_HEADER_WITHDRAWAL_REWARDSCORE_CODE + phoneNo;
            String code = CacheService.StringKey.getObject(key, String.class);
            if (StringUtils.isBlank(code)) {
                throw new DataValidateException("请重新获取短信验证码");
            }
            if (!code.equals(verificationCode)) {
                throw new DataValidateException("短信验证码不正确");
            }
            // 积分提现
            serviceResult = memberService.withdrawalRewardScore(phoneNo, withdrawalInfoDto);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            serviceResult.setMessage(ServiceResult.ERROR_MSG);
            WayLogger.error(e, "积分提现失败," + "请求参数：phoneNo：" + phoneNo + "withdrawalInfoDto：" + withdrawalInfoDto);
        } finally {
            WayLogger.access("积分提现：/withdrawalRewardScore.do,参数：phoneNo：" + phoneNo + "withdrawalInfoDto：" + withdrawalInfoDto);
        }
        return serviceResult;
    }

    /**
     * 获取积分提现记录
     * @param request
     * @return
     */
    @RequestMapping(value = "/getWithdrawalRewardScoreInfo", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> getWithdrawalRewardScoreInfo(HttpServletRequest request, Integer pageNumber){
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
            // 获取积分提现记录
            serviceResult = memberService.getWithdrawalRewardScoreInfo(phoneNo, pageNumber);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            serviceResult.setMessage(ServiceResult.ERROR_MSG);
            WayLogger.error(e, "积分提现失败," + "请求参数：phoneNo：" + phoneNo + "pageNumber：" + pageNumber);
        } finally {
            WayLogger.access("积分提现：/withdrawalRewardScore.do,参数：phoneNo：" + phoneNo + "pageNumber：" + pageNumber);
        }
        return serviceResult;
    }
}
