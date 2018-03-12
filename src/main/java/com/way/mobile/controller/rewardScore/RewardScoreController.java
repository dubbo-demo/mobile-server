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
        String invitationCode = (String) request.getAttribute("invitationCode");
        try {
            // 校验token
            if (StringUtils.isBlank(invitationCode)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (null == pageNumber) { // 第几页为空
                pageNumber = 1;
            }
            // 查看积分明细
            serviceResult = memberService.getRewardScoreDetail(invitationCode, pageNumber);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            serviceResult.setMessage(ServiceResult.ERROR_MSG);
            WayLogger.error(e, "查看积分明细失败," + "请求参数：invitationCode：" + invitationCode + "pageNumber：" + pageNumber);
        } finally {
            WayLogger.access("查看积分明细：/getRewardScoreDetail.do,参数：invitationCode：" + invitationCode + "pageNumber：" + pageNumber);
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
        String invitationCode = (String) request.getAttribute("invitationCode");
        try {
            // 校验token
            if (StringUtils.isBlank(invitationCode)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 校验token
            if (StringUtils.isBlank(validityDurationType)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 积分购买会员
            serviceResult = memberService.buyMemberByRewardScore(invitationCode, validityDurationType);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            serviceResult.setMessage(ServiceResult.ERROR_MSG);
            WayLogger.error(e, "积分购买会员失败," + "请求参数：invitationCode：" + invitationCode + "validityDurationType：" + validityDurationType);
        } finally {
            WayLogger.access("积分购买会员：/buyMemberByRewardScore.do,参数：invitationCode：" + invitationCode + "validityDurationType：" + validityDurationType);
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
        String invitationCode = (String) request.getAttribute("invitationCode");
        try {
            // 校验token
            if (StringUtils.isBlank(invitationCode)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(type)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 积分购买增值服务
            serviceResult = memberService.buyValueAddedServiceByRewardScore(invitationCode, type);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            serviceResult.setMessage(ServiceResult.ERROR_MSG);
            WayLogger.error(e, "积分购买增值服务失败," + "请求参数：invitationCode：" + invitationCode + "type：" + type);
        } finally {
            WayLogger.access("积分购买增值服务：/buyValueAddedServiceByRewardScore.do,参数：invitationCode：" + invitationCode + "type：" + type);
        }
        return serviceResult;
    }

    /**
     * 积分转增
     * @param request
     * @param rewardScore
     * @param friendInvitationCode
     * @param verificationCode
     * @return
     */
    @RequestMapping(value = "/transferRewardScoreToFriend", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> transferRewardScoreToFriend(HttpServletRequest request, Double rewardScore, String friendInvitationCode, String verificationCode){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String invitationCode = (String) request.getAttribute("invitationCode");
        try {
            // 校验token
            if (StringUtils.isBlank(invitationCode)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 校验token
            if (null == rewardScore || StringUtils.isBlank(friendInvitationCode)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            String key = ConstantsConfig.JEDIS_HEADER_TRANSFE_RREWARDSCORE_CODE + invitationCode;
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
            serviceResult = memberService.transferRewardScoreToFriend(invitationCode, rewardScore, friendInvitationCode);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            serviceResult.setMessage(ServiceResult.ERROR_MSG);
            WayLogger.error(e, "积分转增失败," + "请求参数：invitationCode：" + invitationCode + "rewardScore：" + rewardScore+ "friendInvitationCode：" + friendInvitationCode);
        } finally {
            WayLogger.access("积分转增：/transferRewardScoreToFriend.do,参数：invitationCode：" + invitationCode + "rewardScore：" + rewardScore + "friendInvitationCode：" + friendInvitationCode);
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
        String invitationCode = (String) request.getAttribute("invitationCode");
        try {
            // 校验token
            if (StringUtils.isBlank(invitationCode)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 校验token
            if (StringUtils.isBlank(withdrawalInfoDto.getBankName()) || StringUtils.isBlank(withdrawalInfoDto.getBankNumber()) ||
                    StringUtils.isBlank(withdrawalInfoDto.getName()) || null == withdrawalInfoDto.getRewardScore()) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if(withdrawalInfoDto.getRewardScore() < 500 || withdrawalInfoDto.getRewardScore() % 100 != 0){
                return ServiceResult.newFailure("提现积分有误");
            }
            String key = ConstantsConfig.JEDIS_HEADER_WITHDRAWAL_REWARDSCORE_CODE + invitationCode;
            String code = CacheService.StringKey.getObject(key, String.class);
            if (StringUtils.isBlank(code)) {
                return ServiceResult.newFailure("请重新获取短信验证码");
            }
            if (!code.equals(verificationCode)) {
                return ServiceResult.newFailure("短信验证码不正确");
            }
            // 积分提现
            serviceResult = memberService.withdrawalRewardScore(invitationCode, withdrawalInfoDto);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            serviceResult.setMessage(ServiceResult.ERROR_MSG);
            WayLogger.error(e, "积分提现失败," + "请求参数：invitationCode：" + invitationCode + "withdrawalInfoDto：" + withdrawalInfoDto);
        } finally {
            WayLogger.access("积分提现：/withdrawalRewardScore.do,参数：invitationCode：" + invitationCode + "withdrawalInfoDto：" + withdrawalInfoDto);
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
        String invitationCode = (String) request.getAttribute("invitationCode");
        try {
            // 校验token
            if (StringUtils.isBlank(invitationCode)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (null == pageNumber) { // 第几页为空
                pageNumber = 1;
            }
            // 获取积分提现记录
            serviceResult = memberService.getWithdrawalRewardScoreInfo(invitationCode, pageNumber);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            serviceResult.setMessage(ServiceResult.ERROR_MSG);
            WayLogger.error(e, "积分提现失败," + "请求参数：invitationCode：" + invitationCode + "pageNumber：" + pageNumber);
        } finally {
            WayLogger.access("积分提现：/getWithdrawalRewardScoreInfo.do,参数：invitationCode：" + invitationCode + "pageNumber：" + pageNumber);
        }
        return serviceResult;
    }
}
