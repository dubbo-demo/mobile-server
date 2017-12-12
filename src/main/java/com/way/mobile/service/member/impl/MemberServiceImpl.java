package com.way.mobile.service.member.impl;

import com.way.common.constant.Constants;
import com.way.common.result.ServiceResult;
import com.way.common.util.DateUtils;
import com.way.member.friend.dto.FriendsInfoDto;
import com.way.member.friend.service.FriendsInfoService;
import com.way.member.member.dto.MemberDto;
import com.way.member.member.service.MemberInfoService;
import com.way.member.recharge.dto.RechargeInfoDto;
import com.way.member.recharge.service.RechargeInfoService;
import com.way.member.rewardScore.dto.RewardScoreDto;
import com.way.member.rewardScore.service.RewardScoreService;
import com.way.member.withdrawal.dto.WithdrawalInfoDto;
import com.way.mobile.service.member.MemberService;
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
        ServiceResult<MemberDto> serviceResult = memberInfoService.searchUserByPhoneNo(friendPhoneNo);
        if(serviceResult.getData() != null){
            // 判断用户是否为好友
            ServiceResult<FriendsInfoDto> friendsInfoDto = friendsInfoService.getFriendInfo(phoneNo, friendPhoneNo);
            if(null != friendsInfoDto.getData()){
                serviceResult.getData().setIsFriend(Constants.YES);
            }else{
                serviceResult.getData().setIsFriend(Constants.NO);
            }
        }
        return serviceResult;
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
        // 查询总页数
        Integer count = rewardScoreService.getRewardScoreDetailCount(phoneNo);
        // 分页查询
        List<RewardScoreDto> details = rewardScoreService.getRewardScoreDetailList(phoneNo, pageNumber - 1);
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
        if(null != memberDto.getData().getMemberEndTime()){
            startTime = memberDto.getData().getMemberEndTime();
        }
        // 根据会员类型计算充值后会员有效期
        Date endTime = DateUtils.dayEnd(DateUtils.addDays(DateUtils.addMonths(startTime,3), 1));

        if(null != memberDto.getData().getMemberStartTime()){
            startTime = memberDto.getData().getMemberStartTime();
        }
        // 积分购买会员
        memberInfoService.buyMemberByRewardScore(phoneNo, rewardScore, startTime, endTime, name);
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
        ServiceResult<MemberDto> memberDto = getMemberInfo(phoneNo);
        if(!memberDto.getData().getMemberType().equals("2")){
            return ServiceResult.newFailure("您还不是正式会员");
        }
        // 获取增值服务有效期开始时间
        Date startTime = new Date();
        if(null != memberDto.getData().getValueAddedServiceEndTime()){
            startTime = memberDto.getData().getValueAddedServiceEndTime();
        }
        // 获取会员结束时间
        Date endTime = memberDto.getData().getMemberEndTime();
        // 计算开始时间和结束时间所差的天数
        int day = (int)DateUtils.getDoubleSubDays(startTime, endTime);
        if(day <= 0){
            return ServiceResult.newFailure("增值服务已达最大使用期限，无需购买");
        }
//        double amount = (type);TODO
        // 根据会员有效期类型获取所需积分
        Double rewardScore = new BigDecimal(day).multiply(new BigDecimal(0.5)).doubleValue();
        if(memberDto.getData().getRewardScore() - rewardScore < 0){
            return ServiceResult.newFailure("积分不够");
        }
        String name = day + "天增值服务";

        // 积分购买增值服务
        memberInfoService.buyValueAddedServiceByRewardScore(phoneNo, rewardScore, startTime, endTime, name, type);
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
        if(null == memberInfoService.searchUserByPhoneNo(friendPhoneNo).getData()){
            return ServiceResult.newFailure("该手机号不存在");
        }
        // 积分转增
        memberInfoService.transferRewardScoreToFriend(phoneNo, rewardScore, friendPhoneNo);
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
        withdrawalInfoDto.setPhoneNo(phoneNo);
        memberInfoService.withdrawalRewardScore(withdrawalInfoDto);
        return ServiceResult.newSuccess();
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
        // 查询总页数
        Integer count = rechargeInfoService.getRechargeInfoCount(phoneNo);
        // 分页查询
        List<RechargeInfoDto> details = rechargeInfoService.getRechargeInfoList(phoneNo, pageNumber - 1);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("pageCount", count);
        data.put("pageNumber", pageNumber);
        data.put("details", details);
        serviceResult.setData(data);
        return serviceResult;
    }

    /**
     * 充值购买会员
     * @param phoneNo
     * @param validityDurationType
     * @return
     */
    @Override
    public ServiceResult<Object> buyMemberByRecharge(String phoneNo, String validityDurationType) {

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
        if(null != memberDto.getData().getMemberEndTime()){
            startTime = memberDto.getData().getMemberEndTime();
        }
        // 根据会员类型计算充值后会员有效期
        Date endTime = DateUtils.addDays(DateUtils.addWeeks(startTime,3), 1);

        if(null != memberDto.getData().getMemberStartTime()){
            startTime = memberDto.getData().getMemberStartTime();
        }
        // 积分购买会员
        memberInfoService.buyMemberByRewardScore(phoneNo, rewardScore, startTime, endTime, name);
        return ServiceResult.newSuccess();
    }

    /**
     * 充值购买增值服务
     * @param phoneNo
     * @return
     */
    @Override
    public ServiceResult<Object> buyValueAddedServiceByRecharge(String phoneNo) {
        return null;
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
            return 10.0;
        }
        if("2".equals(validityDurationType)){
            return 30.0;
        }
        if("3".equals(validityDurationType)){
            return 100.0;
        }
        return 999999999.0;
    }
}
