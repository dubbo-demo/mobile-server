package com.way.mobile.service.member.impl;

import com.way.common.constant.Constants;
import com.way.common.result.ServiceResult;
import com.way.member.friend.dto.FriendsInfoDto;
import com.way.member.friend.service.FriendsInfoService;
import com.way.member.member.dto.MemberDto;
import com.way.member.member.service.MemberInfoService;
import com.way.member.rewardScore.dto.RewardScoreDto;
import com.way.member.rewardScore.service.RewardScoreService;
import com.way.mobile.service.member.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        String rewardScore = getRewardScore(validityDurationType);
        // 查询会员积分
        ServiceResult<MemberDto> memberDto = getMemberInfo(phoneNo);
        if(memberDto.getData().getRewardScore().compareTo(rewardScore) < 0){
            return ServiceResult.newFailure("积分不够");
        }
        // 扣除积分并且给用户设置为会员

        // 积分明细增加记录
        return null;
    }

    /**
     * 积分购买会员
     * @param validityDurationType
     * @return
     */
    private String getRewardScore(String validityDurationType) {
        if("1".equals(validityDurationType)){
            return "10";
        }
        if("2".equals(validityDurationType)){
            return "30";
        }
        if("3".equals(validityDurationType)){
            return "100";
        }
        if("4".equals(validityDurationType)){
            return "300";
        }
        return "99999999999999";
    }
}
