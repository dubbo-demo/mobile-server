package com.way.mobile.service.friend.impl;

import com.way.common.constant.Constants;
import com.way.common.constant.NumberConstants;
import com.way.common.result.ServiceResult;
import com.way.member.friend.dto.FriendsInfoDto;
import com.way.member.friend.service.FriendsInfoService;
import com.way.member.member.dto.MemberDto;
import com.way.member.member.service.MemberInfoService;
import com.way.member.position.service.PositionInfoService;
import com.way.mobile.service.friend.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述：好友ServiceImpl
 *
 * @author xinpei.xu
 * @date 2017/08/31 20:03
 */
@Service
public class FriendServiceImpl implements FriendService {

    @Autowired
    private FriendsInfoService friendsInfoService;

    @Autowired
    private MemberInfoService memberInfoService;

    /**
     * 获取首页好友以及组信息
     * @param phoneNo
     * @return
     */
    @Override
    public ServiceResult<Object> getFriendsAndGroups(String phoneNo) {
        return null;
    }

    /**
     * 取消查看好友实时坐标
     * @param phoneNo
     * @param friendPhoneNo
     * @return
     */
    @Override
    public ServiceResult<Object> cancelGetFriendPosition(String phoneNo, String friendPhoneNo) {
        friendsInfoService.updateIsCheckBeforeExitByFriendPhoneNo(phoneNo, friendPhoneNo, Constants.NO_INT);
        return ServiceResult.newSuccess();
    }

    /**
     * 取消查看组好友实时坐标
     * @param phoneNo
     * @param groupId
     * @return
     */
    @Override
    public ServiceResult<Object> cancelGetGroupPosition(String phoneNo, String groupId) {
        friendsInfoService.updateIsCheckBeforeExitByGroupId(phoneNo, groupId, Constants.NO_INT);
        return ServiceResult.newSuccess();
    }

    /**
     * 查询手机联系人状态
     * @param phoneNo
     * @param friendPhoneNo
     * @return
     */
    @Override
    public ServiceResult<Object> getPhoneContactStatus(String phoneNo, String friendPhoneNo) {
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        Map<String, String> map = new HashMap<String, String>();
        // 是否注册APP
        ServiceResult<MemberDto> memberDto = memberInfoService.queryMemberInfo(friendPhoneNo);
        if(null == memberDto.getData()){
            map.put("status", NumberConstants.STR_TWO);
            serviceResult.setData(map);
            return serviceResult;
        }
        // 查询好友信息
        ServiceResult<FriendsInfoDto> friendsInfoDto = friendsInfoService.getFriendInfo(phoneNo, friendPhoneNo);
        if (null == friendsInfoDto.getData()){
            map.put("status", NumberConstants.STR_THREE);
            serviceResult.setData(map);
            return serviceResult;
        }
        map.put("status", NumberConstants.STR_ONE);
        serviceResult.setData(map);
        return serviceResult;
    }
}
