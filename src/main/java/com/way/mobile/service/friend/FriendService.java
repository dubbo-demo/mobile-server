package com.way.mobile.service.friend;

import com.way.common.result.ServiceResult;

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
     * @param phoneNo
     * @param friendPhoneNo
     * @return
     */
    ServiceResult<Object> agreeToAddFriend(String phoneNo, String friendPhoneNo, String isApprove);
}
