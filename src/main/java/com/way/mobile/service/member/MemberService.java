package com.way.mobile.service.member;

import com.way.common.result.ServiceResult;
import com.way.member.withdrawal.dto.WithdrawalInfoDto;
import com.way.member.member.dto.MemberDto;

/**
 * @ClassName: MemberService
 * @Description: 用户信息Service
 * @author: xinpei.xu
 * @date: 2017/08/21 19:42
 *
 */
public interface MemberService {

    /**
     * 校验邀请人手机号是否存在
     * @param phoneNo
     * @return
     */
    ServiceResult<String> checkPhone(String phoneNo);

    /**
     * 根据手机号搜索用户
     *
     * @param invitationCode
     * @param friendInvitationCode
     * @return
     */
    ServiceResult<MemberDto> searchUserByPhoneNo(String invitationCode, String friendInvitationCode);

    /**
     * 查看个人信息
     * @param invitationCode
     * @return
     */
    ServiceResult<MemberDto> getMemberInfo(String invitationCode);

    /**
     * 修改个人信息
     * @param dto
     * @return
     */
    ServiceResult<Object> modifyMemberInfo(MemberDto dto);

    /**
     * 查看积分明细
     * @param invitationCode
     * @param pageNumber
     * @return
     */
    ServiceResult<Object> getRewardScoreDetail(String invitationCode, Integer pageNumber);

    /**
     * 积分购买会员
     * @param invitationCode
     * @param validityDurationType
     * @return
     */
    ServiceResult<Object> buyMemberByRewardScore(String invitationCode, String validityDurationType);

    /**
     * 积分购买增值服务
     * @param invitationCode
     * @param type
     * @return
     */
    ServiceResult<Object> buyValueAddedServiceByRewardScore(String invitationCode, String type);

    /**
     * 积分转增
     * @param invitationCode
     * @param rewardScore
     * @param friendInvitationCode
     * @return
     */
    ServiceResult<Object> transferRewardScoreToFriend(String invitationCode, Double rewardScore, String friendInvitationCode);

    /**
     * 积分提现
     * @param invitationCode
     * @param withdrawalInfoDto
     * @return
     */
    ServiceResult<Object> withdrawalRewardScore(String invitationCode, WithdrawalInfoDto withdrawalInfoDto);

    /**
     * 查看充值记录
     * @param invitationCode
     * @param pageNumber
     * @return
     */
    ServiceResult<Object> getRechargeInfo(String invitationCode, Integer pageNumber);

    /**
     * APP获取购买订单号
     * @param invitationCode
     * @param type
     * @param validityDurationType
     * @return
     */
    ServiceResult<Object> getOrderNumber(String invitationCode, String type, String validityDurationType);

    /**
     * 充值购买会员/增值服务
     * @param phoneNo
     * @param type
     * @param validityDurationType
     * @return
     */
    ServiceResult<Object> buyServiceByRecharge(String phoneNo, Integer type, Integer validityDurationType);

    /**
     * 获取积分提现记录
     * @param invitationCode
     * @param pageNumber
     * @return
     */
    ServiceResult<Object> getWithdrawalRewardScoreInfo(String invitationCode, Integer pageNumber);

    /**
     * 查看用户增值服务时间
     * @param invitationCode
     * @param type
     * @return
     */
    ServiceResult<Object> getMemberValueAddedTime(String invitationCode, String type);
}
