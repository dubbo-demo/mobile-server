package com.way.mobile.service.friend;

import com.way.common.result.ServiceResult;
import com.way.member.friend.dto.FriendsInfoDto;
import com.way.member.friend.dto.GroupInfoDto;

/**
 * 功能描述：好友Service
 *
 * @author xinpei.xu
 * @date 2017/08/31 20:01
 */
public interface FriendService {

    /**
     * 获取首页好友以及组信息
     * @param phoneNo
     * @return
     */
    ServiceResult<Object> getFriendsAndGroups(String phoneNo);

    /**
     * 取消查看好友实时坐标
     * @param phoneNo
     * @param friendPhoneNo
     * @return
     */
    ServiceResult<Object> cancelGetFriendPosition(String phoneNo, String friendPhoneNo);

    /**
     * 取消查看组好友实时坐标
     * @param phoneNo
     * @param groupId
     * @return
     */
    ServiceResult<Object> cancelGetGroupPosition(String phoneNo, String groupId);

    /**
     * 查询手机联系人状态
     * @param phoneNo
     * @param friendPhoneNo
     * @return
     */
    ServiceResult<Object> getPhoneContactStatus(String phoneNo, String friendPhoneNo);

    /**
     * 查询好友列表
     * @param phoneNo
     * @return
     */
    ServiceResult<Object> getFriendList(String phoneNo);

    /**
     * 申请添加好友
     * @param phoneNo
     * @param friendPhoneNo
     * @param applyInfo
     * @return
     */
    ServiceResult<Object> applyForAddFriend(String phoneNo, String friendPhoneNo, String applyInfo);

    /**
     * 获取被申请好友记录
     * @param phoneNo
     * @return
     */
    ServiceResult<Object> getApplicationRecordOfFriend(String phoneNo);

    /**
     * 同意/拒绝添加好友申请
     *
     * @param phoneNo
     * @param friendPhoneNo
     * @param isApprove
     * @param applicationId
     * @return
     */
    ServiceResult<Object> agreeToAddFriend(String phoneNo, String friendPhoneNo, String isApprove, String applicationId);

    /**
     * 修改好友信息
     * @param phoneNo
     * @param dto
     * @return
     */
    ServiceResult<Object> modifyFriendInfo(String phoneNo, FriendsInfoDto dto);

    /**
     * 删除好友
     * @param phoneNo
     * @param friendPhoneNo
     * @return
     */
    ServiceResult<Object> deleteFriend(String phoneNo, String friendPhoneNo);

    /**
     * 新建组
     * @param phoneNo
     * @param groupName
     * @return
     */
    ServiceResult<Object> addGroupInfo(String phoneNo, String groupName);

    /**
     * 查看组信息
     * @param phoneNo
     * @param groupId
     * @return
     */
    ServiceResult<GroupInfoDto> getGroupInfo(String phoneNo, String groupId);

    /**
     * 修改组信息
     * @param phoneNo
     * @param dto
     * @return
     */
    ServiceResult<GroupInfoDto> modifyGroupInfo(String phoneNo, GroupInfoDto dto);

    /**
     * 删除组信息
     * @param phoneNo
     * @param groupId
     * @return
     */
    ServiceResult<GroupInfoDto> deleteGroupInfo(String phoneNo, String groupId);

    /**
     * 将好友添加到分组
     * @param phoneNo
     * @param groupId
     * @param friendPhoneNo
     * @return
     */
    ServiceResult<Object> moveFriendToGroup(String phoneNo, String groupId, String friendPhoneNo);

    /**
     * 将好友从分组中移除
     * @param phoneNo
     * @param groupId
     * @param friendPhoneNo
     * @return
     */
    ServiceResult<Object> removeFriendFromGroup(String phoneNo, String groupId, String friendPhoneNo);
}
