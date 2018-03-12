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
import com.way.mobile.service.member.MemberService;
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

    @Autowired
    private MemberService memberService;

    /**
     * 获取首页好友以及组信息
     * @param invitationCode
     * @return
     */
    @Override
    public ServiceResult<Object> getFriendsAndGroups(String invitationCode) {
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        // 查询用户信息
//        ServiceResult<MemberDto> memberDto = memberService.getMemberInfo(phoneNo);
//        String invitationCode = memberDto.getData().getInvitationCode();
        Map<String, Object> data = new HashMap<String, Object>();
        // 非组成员好友
        List<FriendsInfoDto> notGroupFriendslist = new ArrayList<FriendsInfoDto>();
        List<GroupInfoDto> groupInfoDtos = new ArrayList<GroupInfoDto>();
        // 查询好友列表
        List<FriendsInfoDto> friendsInfoDtos = friendsInfoService.getFriendList(invitationCode);
        // 查询组信息
        List<GroupInfoDto> groups = groupInfoService.getGroupInfoListByInvitationCode(invitationCode);
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
            groupInfoDto.setFriends(groupFriendsList);
            groupInfoDtos.add(groupInfoDto);
        }
        data.put("groups", groupInfoDtos);
        serviceResult.setData(data);
        return serviceResult;
    }

    /**
     * 取消查看好友实时坐标
     * @param invitationCode
     * @param friendInvitationCodes
     * @return
     */
    @Override
    public ServiceResult<Object> cancelGetFriendPosition(String invitationCode, List<String> friendInvitationCodes) {
        // 标记好友退出前查看为否：2
        friendsInfoService.updateIsCheckBeforeExitByFriendPhoneNos(invitationCode, friendInvitationCodes, Constants.NO_INT);
        return ServiceResult.newSuccess();
    }

    /**
     * 取消查看组好友实时坐标
     * @param invitationCode
     * @param groupId
     * @return
     */
    @Override
    public ServiceResult<Object> cancelGetGroupPosition(String invitationCode, String groupId) {
        // 标记组好友退出前查看为否：2
        friendsInfoService.updateIsCheckBeforeExitByGroupId(invitationCode, groupId, Constants.NO_INT);
        return ServiceResult.newSuccess();
    }

    /**
     * 设置好友为退出前可见
     * @param invitationCode
     * @param friendInvitationCodeList
     * @return
     */
    @Override
    public ServiceResult<Object> setFriendsVisibleBeforeExiting(String invitationCode, List<String> friendInvitationCodeList) {
//        ServiceResult<MemberDto> memberDto = memberService.getMemberInfo(invitationCode);
//        String invitationCode = memberDto.getData().getInvitationCode();
        // 查出退出前查看的好友信息
        List<FriendsInfoDto> friendsInfoDtos = friendsInfoService.getFriendsInfoBeforeExit(invitationCode);
        List<String> visibleFriendsList = new ArrayList<String>();
        for(FriendsInfoDto friendsInfoDto : friendsInfoDtos){
            visibleFriendsList.add(friendsInfoDto.getFriendInvitationCode());
        }
        // 需要设置为退出前可见的好友邀请码
        List<String> setVisibleFriendsList = friendInvitationCodeList;
        // 已经设为退出前可见的好友
        List<String> visibledFriendsList = new ArrayList<String>();
        // 需要设置为退出前不可见的好友邀请码
        List<String> setInvisibleFriendsList = new ArrayList<String>();
        for(String friendPhone : visibleFriendsList) {
            if(setVisibleFriendsList.contains(friendPhone)){
                // 已经设为退出前可见的好友
                visibledFriendsList.add(friendPhone);
            }else{
                // 需要设为退出前不可见的好友
                setInvisibleFriendsList.add(friendPhone);
            }
        }
        // 需要设为退出前可见的好友
        setVisibleFriendsList.removeAll(visibledFriendsList);
        // 设置好友为退出前可见
        friendsInfoService.setFriendsVisibleBeforeExiting(invitationCode, setInvisibleFriendsList, setVisibleFriendsList);
        return ServiceResult.newSuccess();
    }

    /**
     * 查询手机联系人状态
     * @param invitationCode
     * @param friendPhoneNo
     * @return
     */
    @Override
    public ServiceResult<Object> getPhoneContactStatus(String invitationCode, String friendPhoneNo) {
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
//        ServiceResult<MemberDto> memberDto = memberInfoService.getMemberInfo(phoneNo);
//        String invitationCode = memberDto.getData().getInvitationCode();
        Map<String, String> map = new HashMap<String, String>();
        // 是否注册APP
        ServiceResult<MemberDto> friendMemberDto = memberInfoService.getMemberInfo(friendPhoneNo);
        if(null == friendMemberDto.getData()){
            map.put("status", NumberConstants.STR_TWO);
            serviceResult.setData(map);
            return serviceResult;
        }
        // 查询好友信息
        ServiceResult<FriendsInfoDto> friendsInfoDto = friendsInfoService.getFriendInfo(invitationCode, friendMemberDto.getData().getInvitationCode());
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
     * @param invitationCode
     * @return
     */
    @Override
    public ServiceResult<Object> getFriendList(String invitationCode) {
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        // 查询用户信息
//        ServiceResult<MemberDto> memberDto = memberService.getMemberInfo(phoneNo);
//        String invitationCode = memberDto.getData().getInvitationCode();
        Map<String, List<FriendsInfoDto>> map = new HashMap<String, List<FriendsInfoDto>>();
        List<FriendsInfoDto> friendsInfoDtos = friendsInfoService.getFriendList(invitationCode);
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
     * @param invitationCode
     * @param friendInvitationCode
     * @param applyInfo
     * @return
     */
    @Override
    public ServiceResult<Object> applyForAddFriend(String invitationCode, String friendInvitationCode, String applyInfo) {
        // 查询用户信息
//        ServiceResult<MemberDto> memberDto = memberService.getMemberInfo(phoneNo);
//        String invitationCode = memberDto.getData().getInvitationCode();

        ServiceResult<MemberDto> friendMemberDto = memberService.getMemberInfo(friendInvitationCode);
        if(null == friendMemberDto.getData()){
            return ServiceResult.newFailure("该用户不存在");
        }
//        String friendInvitationCode = friendMemberDto.getData().getInvitationCode();

        // 判断是否为好友
        ServiceResult<FriendsInfoDto> friendsInfoDto = friendsInfoService.getFriendInfo(invitationCode, friendInvitationCode);
        if(null != friendsInfoDto.getData()){
            return ServiceResult.newFailure("该用户已经是你的好友");
        }

        // 申请添加好友
        applyFriendInfoService.applyForAddFriend(invitationCode, friendInvitationCode, applyInfo);
        return ServiceResult.newSuccess();
    }

    /**
     * 获取被申请好友记录
     * @param invitationCode
     * @return
     */
    @Override
    public ServiceResult<Object> getApplicationRecordOfFriend(String invitationCode) {
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        // 查询用户信息
//        ServiceResult<MemberDto> memberDto = memberService.getMemberInfo(phoneNo);
//        String invitationCode = memberDto.getData().getInvitationCode();

        Map<String, List<FriendsInfoDto>> map = new HashMap<String, List<FriendsInfoDto>>();
        // 获取被申请好友记录
        List<FriendsInfoDto> friendsInfoDtos = applyFriendInfoService.getApplicationRecordOfFriend(invitationCode);
        map.put("records", friendsInfoDtos);
        serviceResult.setData(map);
        return serviceResult;
    }

    /**
     * 同意/拒绝添加好友申请
     *
     * @param invitationCode
     * @param friendInvitationCode
     * @param isApprove
     * @param applicationId
     * @return
     */
    @Override
    public ServiceResult<Object> agreeToAddFriend(String invitationCode, String friendInvitationCode, String isApprove, String applicationId) {
        return applyFriendInfoService.agreeToAddFriend(invitationCode, friendInvitationCode, isApprove, applicationId);
    }

    /**
     * 修改好友信息
     * @param invitationCode
     * @param dto
     * @return
     */
    @Override
    @Transactional
    public ServiceResult<Object> modifyFriendInfo(String invitationCode, FriendsInfoDto dto) {
//        ServiceResult<MemberDto> memberDto = memberService.getMemberInfo(phoneNo);
//        String invitationCode = memberDto.getData().getInvitationCode();
        ServiceResult<MemberDto> friendMemberDto = memberService.getMemberInfo(dto.getFriendInvitationCode());
        if(null == friendMemberDto.getData()){
            return ServiceResult.newFailure("该好友不存在");
        }
        dto.setFriendInvitationCode(friendMemberDto.getData().getInvitationCode());
        // 修改好友信息
        friendsInfoService.modifyFriendInfo(invitationCode, dto);
        // 修改被授权人好友信息
        friendsInfoService.modifyAuthorizedFriendInfo(invitationCode, dto);
        return ServiceResult.newSuccess();
    }

    /**
     * 删除好友
     * @param invitationCode
     * @param friendInvitationCode
     * @return
     */
    @Override
    public ServiceResult<Object> deleteFriend(String invitationCode, String friendInvitationCode) {
//        ServiceResult<MemberDto> memberDto = memberService.getMemberInfo(phoneNo);
//        String invitationCode = memberDto.getData().getInvitationCode();
        ServiceResult<MemberDto> friendMemberDto = memberService.getMemberInfo(friendInvitationCode);
        if(null == friendMemberDto.getData()){
            return ServiceResult.newFailure("该好友不存在");
        }
//        String friendInvitationCode = friendMemberDto.getData().getInvitationCode();
        // 删除好友
        friendsInfoService.deleteFriend(invitationCode, friendInvitationCode);
        // 删除对方好友
        friendsInfoService.deleteFriend(friendInvitationCode, invitationCode);
        return ServiceResult.newSuccess();
    }

    /**
     * 新建组
     * @param invitationCode
     * @param groupName
     * @return
     */
    @Override
    public ServiceResult<Object> addGroupInfo(String invitationCode, String groupName) {
//        ServiceResult<MemberDto> memberDto = memberService.getMemberInfo(phoneNo);
//        String invitationCode = memberDto.getData().getInvitationCode();
        return groupInfoService.addGroupInfo(invitationCode, groupName);
    }

    /**
     * 查看组信息
     * @param invitationCode
     * @param groupId
     * @return
     */
    @Override
    public ServiceResult<GroupInfoDto> getGroupInfo(String invitationCode, String groupId) {
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
     * @param invitationCode
     * @param friendInvitationCode
     * @return
     */
    @Override
    public ServiceResult<FriendsInfoDto> getFriendInfo(String invitationCode, String friendInvitationCode) {
//        ServiceResult<MemberDto> memberDto = memberService.getMemberInfo(phoneNo);
//        String invitationCode = memberDto.getData().getInvitationCode();
        ServiceResult<MemberDto> friendMemberDto = memberService.getMemberInfo(friendInvitationCode);
        if(null == friendMemberDto.getData()){
            return ServiceResult.newFailure("该好友不存在");
        }
//        String friendInvitationCode = friendMemberDto.getData().getInvitationCode();
        return friendsInfoService.getFriendInfo(invitationCode, friendInvitationCode);
    }

    /**
     * 修改组信息
     * @param invitationCode
     * @param dto
     * @return
     */
    @Override
    public ServiceResult<GroupInfoDto> modifyGroupInfo(String invitationCode, GroupInfoDto dto) {
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
            modifyFriendInfo(invitationCode, friendsInfoDto);
        }
        return ServiceResult.newSuccess();
    }

    /**
     * 删除组信息
     * @param invitationCode
     * @param groupId
     * @return
     */
    @Override
    @Transactional
    public ServiceResult<GroupInfoDto> deleteGroupInfo(String invitationCode, String groupId) {
//        ServiceResult<MemberDto> memberDto = memberService.getMemberInfo(phoneNo);
//        String invitationCode = memberDto.getData().getInvitationCode();
        // 将好友组信息清空
        friendsInfoService.updateFriendsGroupInfo(invitationCode, groupId);
        // 删除组信息
        groupInfoService.deleteGroupInfo(groupId);
        return ServiceResult.newSuccess();
    }

    /**
     * 将好友添加到分组
     * @param invitationCode
     * @param groupId
     * @param friendInvitationCodes
     * @return
     */
    @Override
    public ServiceResult<Object> moveFriendToGroup(String invitationCode, String groupId, String friendInvitationCodes) {
//        ServiceResult<MemberDto> memberDto = memberService.getMemberInfo(phoneNo);
//        String invitationCode = memberDto.getData().getInvitationCode();
        // 根据组id查出组信息
        GroupInfoDto groupInfoDto = groupInfoService.getGroupInfo(groupId);
        if(null != groupInfoDto){
            groupInfoDto.setGroupId(groupId);
            groupInfoDto.setInvitationCode(invitationCode);
            // 将好友添加到分组
            friendsInfoService.moveFriendToGroup(friendInvitationCodes, groupInfoDto);
        }
        return ServiceResult.newSuccess();
    }

    /**
     * 将好友从分组中移除
     * @param invitationCode
     * @param groupId
     * @param friendInvitationCodes
     * @return
     */
    @Override
    public ServiceResult<Object> removeFriendFromGroup(String invitationCode, String groupId, String friendInvitationCodes) {
        return friendsInfoService.removeFriendFromGroup(invitationCode, friendInvitationCodes);
    }

}
