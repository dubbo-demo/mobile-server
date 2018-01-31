package com.way.mobile.service.member.impl;

import com.way.common.constant.Constants;
import com.way.common.result.ServiceResult;
import com.way.common.util.BeanUtils;
import com.way.common.util.DateUtils;
import com.way.member.friend.dto.FriendsInfoDto;
import com.way.member.friend.service.FriendsInfoService;
import com.way.member.member.dto.MemberDto;
import com.way.member.member.service.MemberInfoService;
import com.way.member.order.dto.MemberOrderInfoDto;
import com.way.member.order.service.MemberOrderInfoService;
import com.way.member.recharge.dto.RechargeInfoDto;
import com.way.member.recharge.service.RechargeInfoService;
import com.way.member.rewardScore.dto.RewardScoreDto;
import com.way.member.rewardScore.service.RewardScoreService;
import com.way.member.valueAdded.dto.MemberValueAddedInfoDto;
import com.way.member.valueAdded.service.MemberValueAddedInfoService;
import com.way.member.withdrawal.dto.WithdrawalInfoDto;
import com.way.mobile.service.member.MemberService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: MemberServiceImpl
 * @Description: 用户信息ServiceImpl
 * @author: xinpei.xu
 * @date: 2017/08/21 19:45
 *
 */
@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberInfoService memberInfoService;

    @Autowired
    private FriendsInfoService friendsInfoService;

    @Autowired
    private RewardScoreService rewardScoreService;

    @Autowired
    private RechargeInfoService rechargeInfoService;

    @Autowired
    private MemberValueAddedInfoService memberValueAddedInfoService;

    @Autowired
    private MemberOrderInfoService memberOrderInfoService;

    /** 轨迹回放金额/天 */
    private static final Double TRAJECTORY_AMOUNT = 0.1;

    /** 电子围栏金额/天 */
    private static final Double FENCE_AMOUNT = 0.1;

    /** 电子围栏金额/天 */
    private static final Double MEMBER_THREE_MOUNTHS_AMOUNT = 15.0;

    /** 电子围栏金额/天 */
    private static final Double MEMBER_HALF_YEAR_AMOUNT = 24.0;

    /** 电子围栏金额/天 */
    private static final Double MEMBER_ONE_YEAR_AMOUNT = 30.0;

    /**
     * 校验邀请人手机号是否存在
     * @param phoneNo
     * @return
     */
    @Override
    public ServiceResult<String> checkPhone(String phoneNo) {
        ServiceResult<String> serviceResult = ServiceResult.newSuccess();
        // 根据手机号查出用户信息
        ServiceResult<MemberDto> memberDto = memberInfoService.queryMemberInfo(phoneNo);
        if(null == memberDto.getData() ){
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            serviceResult.setMessage("校验邀请人手机号不存在");
        }
        return serviceResult;
    }

    /**
     * 根据手机号搜索用户
     *
     * @param phoneNo
     * @param friendPhoneNo
     * @return
     */
    @Override
    public ServiceResult<MemberDto> searchUserByPhoneNo(String phoneNo, String friendPhoneNo) {
        ServiceResult<MemberDto> memberDto = memberInfoService.getMemberInfo(friendPhoneNo);
        String invitationCode = memberDto.getData().getInvitationCode();
        ServiceResult<MemberDto> friendMemberDto = memberInfoService.searchUserByPhoneNo(friendPhoneNo);
        if(friendMemberDto.getData() != null){
            // 判断用户是否为好友
            ServiceResult<FriendsInfoDto> friendsInfoDto = friendsInfoService.getFriendInfo(invitationCode, friendMemberDto.getData().getInvitationCode());
            if(null != friendsInfoDto.getData()){
                friendMemberDto.getData().setIsFriend(Constants.YES);
            }else{
                friendMemberDto.getData().setIsFriend(Constants.NO);
            }
        }
        return friendMemberDto;
    }

    /**
     * 查看个人信息
     * @param phoneNo
     * @return
     */
    @Override
    public ServiceResult<MemberDto> getMemberInfo(String phoneNo) {
        return memberInfoService.getMemberInfo(phoneNo);
    }

    /**
     * 修改个人信息
     * @param dto
     * @return
     */
    @Override
    public ServiceResult<Object> modifyMemberInfo(MemberDto dto) {
        return memberInfoService.modifyMemberInfo(dto);
    }

    /**
     * 查看积分明细
     * @param phoneNo
     * @param pageNumber
     * @return
     */
    @Override
    public ServiceResult<Object> getRewardScoreDetail(String phoneNo, Integer pageNumber) {
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        // 查询总积分
        ServiceResult<MemberDto> memberDto = getMemberInfo(phoneNo);
        String invitationCode = memberDto.getData().getInvitationCode();
        // 查询总页数
        Integer count = rewardScoreService.getRewardScoreDetailCount(invitationCode);
        // 分页查询
        List<RewardScoreDto> details = rewardScoreService.getRewardScoreDetailList(invitationCode, (pageNumber - 1) * 10);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("totalRewardScore", memberDto.getData().getRewardScore());
        data.put("pageCount", count);
        data.put("pageNumber", pageNumber);
        data.put("details", details);
        serviceResult.setData(data);
        return serviceResult;
    }

    /**
     * 积分购买会员
     * @param phoneNo
     * @param validityDurationType
     * @return
     */
    @Override
    public ServiceResult<Object> buyMemberByRewardScore(String phoneNo, String validityDurationType) {
        // 根据会员有效期类型获取所需积分
        Double rewardScore = getRewardScore(validityDurationType);
        String name = getRewardName(validityDurationType);
        // 查询会员积分
        ServiceResult<MemberDto> memberDto = getMemberInfo(phoneNo);
        if(memberDto.getData().getRewardScore() - rewardScore < 0){
            return ServiceResult.newFailure("积分不够");
        }
        // 获取会员有效期开始时间
        Date startTime = new Date();

        if("2".equals(memberDto.getData().getMemberType()) && null != memberDto.getData().getMemberStartTime() &&
                null != memberDto.getData().getMemberEndTime() && startTime.before(memberDto.getData().getMemberEndTime())){
            startTime = memberDto.getData().getMemberEndTime();
        }
        // 根据会员类型计算充值后会员有效期
        Date endTime = DateUtils.dayEnd(DateUtils.addDays(DateUtils.addMonths(startTime,3), 1));
        if("2".equals(validityDurationType)){
            endTime = DateUtils.dayEnd(DateUtils.addDays(DateUtils.addMonths(startTime,6), 1));
        }
        if("3".equals(validityDurationType)){
            endTime = DateUtils.dayEnd(DateUtils.addDays(DateUtils.addMonths(startTime,12), 1));
        }
        // 积分购买会员
        memberInfoService.buyMemberByRewardScore(phoneNo, memberDto.getData(), rewardScore, startTime, endTime, name);
        return ServiceResult.newSuccess();
    }

    /**
     * 积分购买增值服务
     * @param phoneNo
     * @param type
     * @return
     */
    @Override
    public ServiceResult<Object> buyValueAddedServiceByRewardScore(String phoneNo, String type) {
        // 查询会员积分
        ServiceResult<MemberDto> memberDto = memberInfoService.getMemberInfo(phoneNo);
        if(!memberDto.getData().getMemberType().equals("2")){
            return ServiceResult.newFailure("您还不是正式会员");
        }
        String invitationCode = memberDto.getData().getInvitationCode();

        // 根据增值服务类型获取用户增值服务信息
        MemberValueAddedInfoDto memberValueAddedInfoDto = memberValueAddedInfoService.getMemberValueAddedInfoByType(invitationCode, type);
        int day = 0;
        // 获取增值服务有效期开始时间
        Date startTime = new Date();
        // 获取会员结束时间
        Date endTime = memberDto.getData().getMemberEndTime();
        if(null == memberValueAddedInfoDto){
            // 计算开始时间和结束时间所差的天数
            day = (int)DateUtils.getDoubleSubDays(startTime, endTime);
        }
        if(null != memberValueAddedInfoDto){
            if(memberValueAddedInfoDto.getIsOpen() == 1 || memberValueAddedInfoDto.getIsOpen() == 3){
                day = (int)DateUtils.getDoubleSubDays(memberValueAddedInfoDto.getEndTime(), endTime);
                // 判断增值服务是否需要购买
                if(day == 0){
                    return ServiceResult.newFailure("服务已达最大使用期限，无需购买");
                }
            }
            if(memberValueAddedInfoDto.getIsOpen() == 2){
                // 计算开始时间和结束时间所差的天数
                day = (int)DateUtils.getDoubleSubDays(startTime, endTime);
            }
        }
        // 根据会员有效期类型获取所需积分
        Double rewardScore = null;
        String name = null;

        if("1".equals(type)){
            rewardScore = new BigDecimal(day).multiply(new BigDecimal(TRAJECTORY_AMOUNT)).doubleValue();
            name = day + "天轨迹回放服务";
        }
        if("2".equals(type)){
            rewardScore = new BigDecimal(day).multiply(new BigDecimal(FENCE_AMOUNT)).doubleValue();
            name = day + "天电子围栏服务";
        }
        if(memberDto.getData().getRewardScore() - rewardScore < 0){
            return ServiceResult.newFailure("积分不够");
        }

        // 积分购买增值服务
        memberInfoService.buyValueAddedServiceByRewardScore(phoneNo, memberDto.getData(), rewardScore, startTime, endTime, name, type, memberValueAddedInfoDto);
        return ServiceResult.newSuccess();
    }

    /**
     * 积分转增
     * @param phoneNo
     * @param rewardScore
     * @param friendPhoneNo
     * @return
     */
    @Override
    public ServiceResult<Object> transferRewardScoreToFriend(String phoneNo, Double rewardScore, String friendPhoneNo) {
        // 查询会员积分
        ServiceResult<MemberDto> memberDto = getMemberInfo(phoneNo);
        if(memberDto.getData().getRewardScore() - rewardScore < 0){
            return ServiceResult.newFailure("积分不够");
        }
        // 查询该手机号是否存在
        ServiceResult<MemberDto> friendMemberDto = getMemberInfo(friendPhoneNo);
        if(null == friendMemberDto.getData()){
            return ServiceResult.newFailure("该手机号不存在");
        }
        // 积分转增
        memberInfoService.transferRewardScoreToFriend(memberDto.getData().getInvitationCode(), rewardScore, friendMemberDto.getData().getInvitationCode(), phoneNo, friendPhoneNo);
        return ServiceResult.newSuccess();
    }

    /**
     * 积分提现
     * @param phoneNo
     * @param withdrawalInfoDto
     * @return
     */
    @Override
    public ServiceResult<Object> withdrawalRewardScore(String phoneNo, WithdrawalInfoDto withdrawalInfoDto) {
        ServiceResult<MemberDto> memberDto = memberInfoService.getMemberInfo(phoneNo);
        String invitationCode = memberDto.getData().getInvitationCode();
        withdrawalInfoDto.setPhoneNo(phoneNo);
        withdrawalInfoDto.setInvitationCode(invitationCode);
        memberInfoService.withdrawalRewardScore(withdrawalInfoDto);
        return ServiceResult.newSuccess();
    }

    /**
     * 获取积分提现记录
     * @param phoneNo
     * @param pageNumber
     * @return
     */
    @Override
    public ServiceResult<Object> getWithdrawalRewardScoreInfo(String phoneNo, Integer pageNumber) {
        ServiceResult serviceResult = ServiceResult.newSuccess();
        ServiceResult<MemberDto> memberDto = memberInfoService.getMemberInfo(phoneNo);
        String invitationCode = memberDto.getData().getInvitationCode();
        // 查询总页数
        Integer count = memberInfoService.getWithdrawalRewardScoreCount(invitationCode);
        // 分页查询
        List<WithdrawalInfoDto> details = memberInfoService.getWithdrawalRewardScoreInfo(invitationCode, (pageNumber - 1) * 10);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("pageCount", count);
        data.put("pageNumber", pageNumber);
        data.put("details", details);
        serviceResult.setData(data);
        return serviceResult;
    }

    /**
     * 查看充值记录
     * @param phoneNo
     * @param pageNumber
     * @return
     */
    @Override
    public ServiceResult<Object> getRechargeInfo(String phoneNo, Integer pageNumber) {
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        ServiceResult<MemberDto> memberDto = memberInfoService.getMemberInfo(phoneNo);
        String invitationCode = memberDto.getData().getInvitationCode();
        // 查询总页数
        Integer count = rechargeInfoService.getRechargeInfoCount(invitationCode);
        // 分页查询
        List<RechargeInfoDto> details = rechargeInfoService.getRechargeInfoList(invitationCode, (pageNumber - 1) * 10);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("pageCount", count);
        data.put("pageNumber", pageNumber);
        data.put("details", details);
        serviceResult.setData(data);
        return serviceResult;
    }

    /**
     * APP获取购买订单号
     * @param phoneNo
     * @param type
     * @param validityDurationType
     * @return
     */
    @Override
    public ServiceResult<Object> getOrderNumber(String phoneNo, String type, String validityDurationType) {
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        Map<String, String> map = new HashMap<String, String>();
        String orderNumber = BeanUtils.getUUID();
        // 计算出需要的费用
        Double amount = 0.0;
        if("0".equals(type)){
            amount = getRewardScore(validityDurationType);
        }else{
            // 查询会员积分
            ServiceResult<MemberDto> memberDto = memberInfoService.getMemberInfo(phoneNo);
            if(!memberDto.getData().getMemberType().equals("2")){
                return ServiceResult.newFailure("您还不是正式会员");
            }
            // 根据增值服务类型获取用户增值服务信息
            MemberValueAddedInfoDto memberValueAddedInfoDto = memberValueAddedInfoService.getMemberValueAddedInfoByType(memberDto.getData().getInvitationCode(), type);
            int day = 0;
            // 获取增值服务有效期开始时间
            Date startTime = new Date();
            // 获取会员结束时间
            Date endTime = memberDto.getData().getMemberEndTime();
            if(null == memberValueAddedInfoDto){
                // 计算开始时间和结束时间所差的天数
                day = (int)DateUtils.getDoubleSubDays(startTime, endTime);
            }
            if(null != memberValueAddedInfoDto){
                if(memberValueAddedInfoDto.getIsOpen() == 1 || memberValueAddedInfoDto.getIsOpen() == 3){
                    day = (int)DateUtils.getDoubleSubDays(memberValueAddedInfoDto.getEndTime(), endTime);
                    // 判断增值服务是否需要购买
                    if(day == 0){
                        return ServiceResult.newFailure("服务已达最大使用期限，无需购买");
                    }
                }
                if(memberValueAddedInfoDto.getIsOpen() == 2){
                    // 计算开始时间和结束时间所差的天数
                    day = (int)DateUtils.getDoubleSubDays(startTime, endTime);
                }
            }

            if("1".equals(type)){
                amount = day * TRAJECTORY_AMOUNT;
            }
            if("2".equals(type)){
                amount = day * FENCE_AMOUNT;
            }
        }

        // 生成订单信息
        MemberOrderInfoDto memberOrderInfoDto = new MemberOrderInfoDto();
        memberOrderInfoDto.setOrderNumber(orderNumber);
        memberOrderInfoDto.setPhoneNo(phoneNo);
        memberOrderInfoDto.setType(Integer.valueOf(type));
        memberOrderInfoDto.setValidityDurationType(StringUtils.isBlank(validityDurationType) ? null : Integer.valueOf(validityDurationType));
        memberOrderInfoDto.setAmount(amount);
        memberOrderInfoDto.setStatus(0);
        memberOrderInfoDto.setCreateTime(new Date());
        memberOrderInfoDto.setModifyTime(new Date());
        // 保存订单信息
        memberOrderInfoService.saveMemberOrderInfo(memberOrderInfoDto);
        map.put("orderNumber", orderNumber);
        map.put("amount", String.valueOf(amount));
        serviceResult.setData(map);
        return serviceResult;
    }

    /**
     * 充值购买会员/增值服务
     * @param phoneNo
     * @param type
     * @param validityDurationType
     * @return
     */
    @Override
    public ServiceResult<Object> buyServiceByRecharge(String phoneNo, Integer type, Integer validityDurationType) {
        // 计算出需要的费用
        Double amount = 0.0;
        String name = null;
        // 获取增值服务有效期开始时间
        Date startTime = new Date();
        // 获取会员结束时间
        Date endTime = null;
        // 查询会员
        ServiceResult<MemberDto> memberDto = memberInfoService.getMemberInfo(phoneNo);
        if("2".equals(memberDto.getData().getMemberType()) && null != memberDto.getData().getMemberStartTime() &&
                null != memberDto.getData().getMemberEndTime() && startTime.before(memberDto.getData().getMemberEndTime())){
            startTime = memberDto.getData().getMemberEndTime();
        }
        if(0 == type){
            amount = getRewardScore(String.valueOf(validityDurationType));

            if(1 == validityDurationType){
                name = "三个月会员";
                endTime = DateUtils.dayEnd(DateUtils.addMonths(startTime,3));
            }
            if(2 == validityDurationType){
                name = "半年会员";
                endTime = DateUtils.dayEnd(DateUtils.addMonths(startTime,6));
            }
            if(3 == validityDurationType){
                name = "一年会员";
                endTime = DateUtils.dayEnd(DateUtils.addMonths(startTime,12));
            }
        }else{
            if(!memberDto.getData().getMemberType().equals("2")){
                return ServiceResult.newFailure("您还不是正式会员");
            }
            // 根据增值服务类型获取用户增值服务信息
            MemberValueAddedInfoDto memberValueAddedInfoDto = memberValueAddedInfoService.getMemberValueAddedInfoByType(phoneNo, String.valueOf(type));
            int day = 0;
            // 获取会员结束时间
            endTime = memberDto.getData().getMemberEndTime();
            if(null == memberValueAddedInfoDto){
                // 计算开始时间和结束时间所差的天数
                day = (int)DateUtils.getDoubleSubDays(startTime, endTime);
            }
            if(null != memberValueAddedInfoDto){
                if(memberValueAddedInfoDto.getIsOpen() == 1){
                    day = (int)DateUtils.getDoubleSubDays(memberValueAddedInfoDto.getEndTime(), endTime);
                    // 判断增值服务是否需要购买
                    if(day == 0){
                        return ServiceResult.newFailure("服务已达最大使用期限，无需购买");
                    }
                }
                if(memberValueAddedInfoDto.getIsOpen() == 2){
                    // 计算开始时间和结束时间所差的天数
                    day = (int)DateUtils.getDoubleSubDays(startTime, endTime);
                }
            }
            if(1 == type){
                amount = day * TRAJECTORY_AMOUNT;
                name = day + "天轨迹回放服务";
            }
            if(2 == type){
                amount = day * FENCE_AMOUNT;
                name = day + "天电子围栏服务";
            }
        }
        // 充值购买会员/增值服务
        memberInfoService.buyServiceByRecharge(phoneNo, String.valueOf(type), memberDto.getData(), amount, startTime, endTime, name);
        return ServiceResult.newSuccess();
    }


    /**
     * 查看用户增值服务时间
     * @param phoneNo
     * @param type
     * @return
     */
    @Override
    public ServiceResult<Object> getMemberValueAddedTime(String phoneNo, String type) {
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        Map<String ,Object> data = new HashMap<String ,Object>();
        ServiceResult<MemberDto> memberDto = memberInfoService.getMemberInfo(phoneNo);
        String invitationCode = memberDto.getData().getInvitationCode();

        // 根据增值服务类型获取用户增值服务信息
        MemberValueAddedInfoDto memberValueAddedInfoDto = memberValueAddedInfoService.getMemberValueAddedInfoByType(invitationCode, type);
        if(null != memberValueAddedInfoDto){
            data.put("startTime", memberValueAddedInfoDto.getStartTime());
            data.put("endTime", memberValueAddedInfoDto.getEndTime());
            serviceResult.setData(data);
        }
        return serviceResult;
    }

    private String getRewardName(String validityDurationType) {
        if("1".equals(validityDurationType)){
            return "三个月";
        }
        if("2".equals(validityDurationType)){
            return "半年";
        }
        if("3".equals(validityDurationType)){
            return "一年";
        }
        return "";
    }

    /**
     * 积分购买会员
     * @param validityDurationType
     * @return
     */
    private Double getRewardScore(String validityDurationType) {
        if("1".equals(validityDurationType)){
            return MEMBER_THREE_MOUNTHS_AMOUNT;
        }
        if("2".equals(validityDurationType)){
            return MEMBER_HALF_YEAR_AMOUNT;
        }
        if("3".equals(validityDurationType)){
            return MEMBER_ONE_YEAR_AMOUNT;
        }
        return 999999999.0;
    }
}
