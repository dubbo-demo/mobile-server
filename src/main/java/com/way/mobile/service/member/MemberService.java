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
     * @param phoneNo
     * @param friendPhoneNo
     * @return
     */
    ServiceResult<MemberDto> searchUserByPhoneNo(String phoneNo, String friendPhoneNo);

    /**
     * 查看个人信息
     * @param phoneNo
     * @return
     */
    ServiceResult<MemberDto> getMemberInfo(String phoneNo);

    /**
     * 修改个人信息
     * @param dto
     * @return
     */
    ServiceResult<Object> modifyMemberInfo(MemberDto dto);

    /**
     * 查看积分明细
     * @param phoneNo
     * @param pageNumber
     * @return
     */
    ServiceResult<Object> getRewardScoreDetail(String phoneNo, Integer pageNumber);

    /**
     * 积分购买会员
     * @param phoneNo
     * @param validityDurationType
     * @return
     */
    ServiceResult<Object> buyMemberByRewardScore(String phoneNo, String validityDurationType);

    /**
     * 积分购买增值服务
     * @param phoneNo
     * @param type
     * @return
     */
    ServiceResult<Object> buyValueAddedServiceByRewardScore(String phoneNo, String type);

    /**
     * 积分转增
     * @param phoneNo
     * @param rewardScore
     * @param friendPhoneNo
     * @return
     */
    ServiceResult<Object> transferRewardScoreToFriend(String phoneNo, Double rewardScore, String friendPhoneNo);

    /**
     * 积分提现
     * @param phoneNo
     * @param withdrawalInfoDto
     * @return
     */
    ServiceResult<Object> withdrawalRewardScore(String phoneNo, WithdrawalInfoDto withdrawalInfoDto);

    /**
     * 查看充值记录
     * @param phoneNo
     * @param pageNumber
     * @return
     */
    ServiceResult<Object> getRechargeInfo(String phoneNo, Integer pageNumber);

    /**
     * APP获取购买订单号
     * @param phoneNo
     * @param type
     * @param validityDurationType
     * @return
     */
    ServiceResult<Object> getOrderNumber(String phoneNo, String type, String validityDurationType);

    /**
     * 充值购买会员
     * @param phoneNo
     * @param validityDurationType
     * @return
     */
    ServiceResult<Object> buyMemberByRecharge(String phoneNo, String validityDurationType);

}
