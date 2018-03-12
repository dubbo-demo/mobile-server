package com.way.mobile.service.friend;

import com.way.common.result.ServiceResult;
import com.way.member.friend.dto.FriendsInfoDto;
import com.way.member.friend.dto.GroupInfoDto;

import java.util.List;

/**
 * 功能描述：好友Service
 *
 * @author xinpei.xu
 * @date 2017/08/31 20:01
 */
public interface FriendService {

    /**
     * 获取首页好友以及组信息
     * @param invitationCode
     * @return
     */
    ServiceResult<Object> getFriendsAndGroups(String invitationCode);

    /**
     * 取消查看好友实时坐标
     * @param invitationCode
     * @param friendInvitationCodes
     * @return
     */
    ServiceResult<Object> cancelGetFriendPosition(String invitationCode, List<String> friendInvitationCodes);

    /**
     * 取消查看组好友实时坐标
     * @param invitationCode
     * @param groupId
     * @return
     */
    ServiceResult<Object> cancelGetGroupPosition(String invitationCode, String groupId);

    /**
     * 设置好友为退出前可见
     * @param invitationCode
     * @param friendInvitationCodeList
     * @return
     */
    ServiceResult<Object> setFriendsVisibleBeforeExiting(String invitationCode, List<String> friendInvitationCodeList);

    /**
     * 查询手机联系人状态
     * @param invitationCode
     * @param friendPhoneNo
     * @return
     */
    ServiceResult<Object> getPhoneContactStatus(String invitationCode, String friendPhoneNo);

    /**
     * 查询好友列表
     * @param invitationCode
     * @return
     */
    ServiceResult<Object> getFriendList(String invitationCode);

    /**
     * 申请添加好友
     * @param invitationCode
     * @param friendInvitationCode
     * @param applyInfo
     * @return
     */
    ServiceResult<Object> applyForAddFriend(String invitationCode, String friendInvitationCode, String applyInfo);

    /**
     * 获取被申请好友记录
     * @param invitationCode
     * @return
     */
    ServiceResult<Object> getApplicationRecordOfFriend(String invitationCode);

    /**
     * 同意/拒绝添加好友申请
     *
     * @param invitationCode
     * @param friendInvitationCode
     * @param isApprove
     * @param applicationId
     * @return
     */
    ServiceResult<Object> agreeToAddFriend(String invitationCode, String friendInvitationCode, String isApprove, String applicationId);

    /**
     * 修改好友信息
     * @param invitationCode
     * @param dto
     * @return
     */
    ServiceResult<Object> modifyFriendInfo(String invitationCode, FriendsInfoDto dto);

    /**
     * 删除好友
     * @param invitationCode
     * @param friendInvitationCode
     * @return
     */
    ServiceResult<Object> deleteFriend(String invitationCode, String friendInvitationCode);

    /**
     * 新建组
     * @param invitationCode
     * @param groupName
     * @return
     */
    ServiceResult<Object> addGroupInfo(String invitationCode, String groupName);

    /**
     * 查看组信息
     * @param invitationCode
     * @param groupId
     * @return
     */
    ServiceResult<GroupInfoDto> getGroupInfo(String invitationCode, String groupId);

    /**
     * 查看好友信息
     * @param invitationCode
     * @param friendInvitationCode
     * @return
     */
    ServiceResult<FriendsInfoDto> getFriendInfo(String invitationCode, String friendInvitationCode);

    /**
     * 修改组信息
     * @param invitationCode
     * @param dto
     * @return
     */
    ServiceResult<GroupInfoDto> modifyGroupInfo(String invitationCode, GroupInfoDto dto);

    /**
     * 删除组信息
     * @param invitationCode
     * @param groupId
     * @return
     */
    ServiceResult<GroupInfoDto> deleteGroupInfo(String invitationCode, String groupId);

    /**
     * 将好友添加到分组
     * @param invitationCode
     * @param groupId
     * @param friendInvitationCodes
     * @return
     */
    ServiceResult<Object> moveFriendToGroup(String invitationCode, String groupId, String friendInvitationCodes);

    /**
     * 将好友从分组中移除
     * @param invitationCode
     * @param groupId
     * @param friendInvitationCodes
     * @return
     */
    ServiceResult<Object> removeFriendFromGroup(String invitationCode, String groupId, String friendInvitationCodes);

}
