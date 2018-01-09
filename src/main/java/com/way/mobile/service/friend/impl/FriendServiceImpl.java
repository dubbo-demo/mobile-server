package com.way.mobile.service.friend.impl;

import com.way.common.constant.Constants;
import com.way.common.constant.NumberConstants;
import com.way.common.result.ServiceResult;
import com.way.common.util.PingYinUtil;
import com.way.member.friend.dto.FriendsInfoDto;
import com.way.member.friend.dto.GroupInfoDto;
import com.way.member.friend.service.ApplyFriendInfoService;
import com.way.member.friend.service.FriendsInfoService;
import com.way.member.friend.service.GroupInfoService;
import com.way.member.member.dto.MemberDto;
import com.way.member.member.service.MemberInfoService;
import com.way.mobile.service.friend.FriendService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    @Autowired
    private ApplyFriendInfoService applyFriendInfoService;

    @Autowired
    private GroupInfoService groupInfoService;

    /**
     * 获取首页好友以及组信息
     * @param phoneNo
     * @return
     */
    @Override
    public ServiceResult<Object> getFriendsAndGroups(String phoneNo) {
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        Map<String, Object> data = new HashMap<String, Object>();
        // 非组成员好友
        List<FriendsInfoDto> notGroupFriendslist = new ArrayList<FriendsInfoDto>();
        List<GroupInfoDto> groupInfoDtos = new ArrayList<GroupInfoDto>();
        // 查询好友列表
        List<FriendsInfoDto> friendsInfoDtos = friendsInfoService.getFriendList(phoneNo);
        // 查询组信息
        List<GroupInfoDto> groups = groupInfoService.getGroupInfoListByPhoneNo(phoneNo);
        for(FriendsInfoDto dto : friendsInfoDtos){
            if(StringUtils.isBlank(dto.getGroupName())){
                notGroupFriendslist.add(dto);
            }
        }
        data.put("friends", notGroupFriendslist);
        friendsInfoDtos.removeAll(notGroupFriendslist);
        for(GroupInfoDto groupInfoDto : groups){
            List<FriendsInfoDto> groupFriendsList = new ArrayList<FriendsInfoDto>();
            boolean flag = true;
            for(FriendsInfoDto dto : friendsInfoDtos){
                if(groupInfoDto.getGroupId().equals(dto.getGroupId())){
                    groupFriendsList.add(dto);
                }
                if(flag && dto.getIsCheckBeforeExit() == 1){
                    groupInfoDto.setIsCheckBeforeExit(1);
                }
                if(dto.getIsCheckBeforeExit() == 2){
                    groupInfoDto.setIsCheckBeforeExit(2);
                    flag = false;
                }
            }
//            if(groupFriendsList.size() > 0){
                groupInfoDto.setFriends(groupFriendsList);
                groupInfoDtos.add(groupInfoDto);
//            }
        }
        data.put("groups", groupInfoDtos);
        serviceResult.setData(data);
        return serviceResult;
    }

    /**
     * 取消查看好友实时坐标
     * @param phoneNo
     * @param friendPhoneNoList
     * @return
     */
    @Override
    public ServiceResult<Object> cancelGetFriendPosition(String phoneNo, List<String> friendPhoneNoList) {
        // 标记好友退出前查看为否：2
        friendsInfoService.updateIsCheckBeforeExitByFriendPhoneNos(phoneNo, friendPhoneNoList, Constants.NO_INT);
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
        // 标记组好友退出前查看为否：2
        friendsInfoService.updateIsCheckBeforeExitByGroupId(phoneNo, groupId, Constants.NO_INT);
        return ServiceResult.newSuccess();
    }

    /**
     * 设置好友为退出前可见
     * @param phoneNo
     * @param friendPhoneNoList
     * @return
     */
    @Override
    public ServiceResult<Object> setFriendsVisibleBeforeExiting(String phoneNo, List<String> friendPhoneNoList) {
        // 查出退出前查看的好友信息
        List<FriendsInfoDto> friendsInfoDtos = friendsInfoService.getFriendsInfoBeforeExit(phoneNo);
        List<String> visibleFriendsList = new ArrayList<String>();
        for(FriendsInfoDto friendsInfoDto : friendsInfoDtos){
            visibleFriendsList.add(friendsInfoDto.getFriendPhoneNo());
        }
        // 需要设置为退出前不可见的好友手机号
        List<String> setVisibleFriendsList = new ArrayList<String>();
        // 已经设为退出前可见的好友
        List<String> setInvisibleFriendsList = new ArrayList<String>();
        for(String friendPhone : visibleFriendsList) {
            if(friendPhoneNoList.contains(friendPhone)){
                // 已经设为退出前可见的好友
                setVisibleFriendsList.add(friendPhone);
            }else{
                // 需要设为退出前不可见的好友
                setInvisibleFriendsList.add(friendPhone);
            }
        }
        // 需要设为退出前可见的好友
        friendPhoneNoList.removeAll(setVisibleFriendsList);
        // 设置好友为退出前可见
        friendsInfoService.setFriendsVisibleBeforeExiting(phoneNo, setInvisibleFriendsList, friendPhoneNoList);
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

    /**
     * 查询好友列表
     * @param phoneNo
     * @return
     */
    @Override
    public ServiceResult<Object> getFriendList(String phoneNo) {
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        Map<String, List<FriendsInfoDto>> map = new HashMap<String, List<FriendsInfoDto>>();
        List<FriendsInfoDto> friendsInfoDtos = friendsInfoService.getFriendList(phoneNo);
        for(FriendsInfoDto friendsInfoDto : friendsInfoDtos){
            friendsInfoDto.setFriendRemarkNameSpell(PingYinUtil.getPingYin(friendsInfoDto.getFriendRemarkName()));
            friendsInfoDto.setRemarkFirstLetter(friendsInfoDto.getFriendRemarkNameSpell().substring(0, 1));
        }
        // 根据姓名拼音升序排列
        Collections.sort(friendsInfoDtos, new Comparator<FriendsInfoDto>(){
            @Override
            public int compare(FriendsInfoDto friend1, FriendsInfoDto friend2) {
                if(friend1.getFriendRemarkNameSpell().compareTo(friend2.getFriendRemarkNameSpell()) > 0){
                    return 1;
                }else if (friend1.getFriendRemarkNameSpell().compareTo(friend2.getFriendRemarkNameSpell()) < 0) {
                    return -1;
                }else{
                    return friend1.getFriendPhoneNo().compareTo(friend2.getFriendPhoneNo());
                }
            }
        });

        map.put("friends", friendsInfoDtos);
        serviceResult.setData(map);
        return serviceResult;
    }

    /**
     * 申请添加好友
     * @param phoneNo
     * @param friendPhoneNo
     * @param applyInfo
     * @return
     */
    @Override
    public ServiceResult<Object> applyForAddFriend(String phoneNo, String friendPhoneNo, String applyInfo) {
        // 判断是否为好友
        ServiceResult<FriendsInfoDto> friendsInfoDto = friendsInfoService.getFriendInfo(phoneNo, friendPhoneNo);
        if(null != friendsInfoDto.getData()){
            return ServiceResult.newFailure("该用户已经是你的好友");
        }
        // 申请添加好友
        applyFriendInfoService.applyForAddFriend(phoneNo, friendPhoneNo, applyInfo);
        return ServiceResult.newSuccess();
    }

    /**
     * 获取被申请好友记录
     * @param phoneNo
     * @return
     */
    @Override
    public ServiceResult<Object> getApplicationRecordOfFriend(String phoneNo) {
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        Map<String, List<FriendsInfoDto>> map = new HashMap<String, List<FriendsInfoDto>>();
        // 获取被申请好友记录
        List<FriendsInfoDto> friendsInfoDtos = applyFriendInfoService.getApplicationRecordOfFriend(phoneNo);
        map.put("records", friendsInfoDtos);
        serviceResult.setData(map);
        return serviceResult;
    }

    /**
     * 同意/拒绝添加好友申请
     *
     * @param phoneNo
     * @param friendPhoneNo
     * @param isApprove
     * @param applicationId
     * @return
     */
    @Override
    public ServiceResult<Object> agreeToAddFriend(String phoneNo, String friendPhoneNo, String isApprove, String applicationId) {
        return applyFriendInfoService.agreeToAddFriend(phoneNo, friendPhoneNo, isApprove, applicationId);
    }

    /**
     * 修改好友信息
     * @param phoneNo
     * @param dto
     * @return
     */
    @Override
    @Transactional
    public ServiceResult<Object> modifyFriendInfo(String phoneNo, FriendsInfoDto dto) {
        // 修改好友信息
        friendsInfoService.modifyFriendInfo(phoneNo, dto);
        // 修改被授权人好友信息
        friendsInfoService.modifyAuthorizedFriendInfo(phoneNo, dto);
        return ServiceResult.newSuccess();
    }

    /**
     * 删除好友
     * @param phoneNo
     * @param friendPhoneNo
     * @return
     */
    @Override
    public ServiceResult<Object> deleteFriend(String phoneNo, String friendPhoneNo) {
        // 删除好友
        friendsInfoService.deleteFriend(phoneNo, friendPhoneNo);
        // 删除对方好友
        friendsInfoService.deleteFriend(friendPhoneNo, phoneNo);
        return ServiceResult.newSuccess();
    }

    /**
     * 新建组
     * @param phoneNo
     * @param groupName
     * @return
     */
    @Override
    public ServiceResult<Object> addGroupInfo(String phoneNo, String groupName) {
        return groupInfoService.addGroupInfo(phoneNo, groupName);
    }

    /**
     * 查看组信息
     * @param phoneNo
     * @param groupId
     * @return
     */
    @Override
    public ServiceResult<GroupInfoDto> getGroupInfo(String phoneNo, String groupId) {
        ServiceResult<GroupInfoDto> serviceResult = ServiceResult.newSuccess();
        // 查出组信息
        GroupInfoDto groupInfo = groupInfoService.getGroupInfo(groupId);
        // 根据组ID获取好友信息
        List<FriendsInfoDto> friendsInfo = friendsInfoService.getFriendListByGroupId(groupId);
        groupInfo.setFriends(friendsInfo);
        serviceResult.setData(groupInfo);
        return serviceResult;
    }

    /**
     * 查看好友信息
     * @param phoneNo
     * @param friendPhoneNo
     * @return
     */
    @Override
    public ServiceResult<FriendsInfoDto> getFriendInfo(String phoneNo, String friendPhoneNo) {

        return friendsInfoService.getFriendInfo(phoneNo, friendPhoneNo);
    }

    /**
     * 修改组信息
     * @param phoneNo
     * @param dto
     * @return
     */
    @Override
    public ServiceResult<GroupInfoDto> modifyGroupInfo(String phoneNo, GroupInfoDto dto) {
        // 修改组信息
        groupInfoService.modifyGroupInfo(dto);
        // 根据组ID获取好友信息
        List<FriendsInfoDto> friendsInfo = friendsInfoService.getFriendListByGroupId(dto.getGroupId());
        for(FriendsInfoDto friendsInfoDto : friendsInfo){
            friendsInfoDto.setIsAccreditVisible(dto.getIsAccreditVisible());
            friendsInfoDto.setAccreditStartTime(dto.getAccreditStartTime());
            friendsInfoDto.setAccreditEndTime(dto.getAccreditEndTime());
            friendsInfoDto.setAccreditWeeks(dto.getAccreditWeeks());
            // 修改好友信息
            modifyFriendInfo(phoneNo, friendsInfoDto);
        }
        return ServiceResult.newSuccess();
    }

    /**
     * 删除组信息
     * @param phoneNo
     * @param groupId
     * @return
     */
    @Override
    @Transactional
    public ServiceResult<GroupInfoDto> deleteGroupInfo(String phoneNo, String groupId) {
        // 将好友组信息清空
        friendsInfoService.updateFriendsGroupInfo(phoneNo, groupId);
        // 删除组信息
        groupInfoService.deleteGroupInfo(groupId);
        return ServiceResult.newSuccess();
    }

    /**
     * 将好友添加到分组
     * @param phoneNo
     * @param groupId
     * @param friendPhoneNos
     * @return
     */
    @Override
    public ServiceResult<Object> moveFriendToGroup(String phoneNo, String groupId, String friendPhoneNos) {
        // 根据组id查出组信息
        GroupInfoDto groupInfoDto = groupInfoService.getGroupInfo(groupId);
        if(null != groupInfoDto){
            groupInfoDto.setGroupId(groupId);
            groupInfoDto.setPhoneNo(phoneNo);
            // 将好友添加到分组
            friendsInfoService.moveFriendToGroup(friendPhoneNos, groupInfoDto);
        }
        return ServiceResult.newSuccess();
    }

    /**
     * 将好友从分组中移除
     * @param phoneNo
     * @param groupId
     * @param friendPhoneNos
     * @return
     */
    @Override
    public ServiceResult<Object> removeFriendFromGroup(String phoneNo, String groupId, String friendPhoneNos) {
        return friendsInfoService.removeFriendFromGroup(phoneNo, friendPhoneNos);
    }

}
