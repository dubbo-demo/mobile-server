package com.way.mobile.controller.friend;

import com.alibaba.fastjson.JSON;
import com.way.common.constant.Constants;
import com.way.common.log.WayLogger;
import com.way.common.result.ServiceResult;
import com.way.member.friend.dto.FriendsInfoDto;
import com.way.member.friend.dto.GroupInfoDto;
import com.way.mobile.service.friend.FriendService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 功能描述：好友Controller
 *
 * @author xinpei.xu
 * @date 2017/08/31 19:56
 */
@Controller
public class FriendController {

    @Autowired
    private FriendService friendService;

    /**
     * 获取首页好友以及组信息
     * @param request
     * @return
     */
    @RequestMapping(value = "/getFriendsAndGroups", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> getFriendsAndGroups(HttpServletRequest request){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String invitationCode = (String) request.getAttribute("invitationCode");
        try {
            // 校验token
            if (StringUtils.isBlank(invitationCode)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 获取首页好友以及组信息
            serviceResult = friendService.getFriendsAndGroups(invitationCode);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            serviceResult.setMessage(ServiceResult.ERROR_MSG);
            WayLogger.error(e, "获取首页好友以及组信息失败," + "请求参数：" + invitationCode);
        } finally {
            WayLogger.access("获取首页好友以及组信息：/getFriendsAndGroups.do,参数：" + invitationCode);
        }
        return serviceResult;
    }

    /**
     * 取消查看好友实时坐标
     * @param request
     * @param friendPhoneNos
     * @return
     */
    @RequestMapping(value = "/cancelGetFriendPosition", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> cancelGetFriendPosition(HttpServletRequest request, String friendInvitationCodes){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String invitationCode = (String) request.getAttribute("invitationCode");
        try {
            // 校验token
            if (StringUtils.isBlank(invitationCode)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(friendInvitationCodes)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            List<String> friendInvitationCodeList = JSON.parseArray(friendInvitationCodes, String.class);
            for(String friendInvitationCode : friendInvitationCodeList){
                if (StringUtils.isBlank(friendInvitationCode)) {
                    return ServiceResult.newFailure("必传参数不能为空");
                }
            }
            // 取消查看好友实时坐标
            serviceResult = friendService.cancelGetFriendPosition(invitationCode, friendInvitationCodeList);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            serviceResult.setMessage(ServiceResult.ERROR_MSG);
            WayLogger.error(e, "取消查看好友实时坐标失败," + "请求参数：invitationCode：" + invitationCode + "，friendInvitationCodes：" + friendInvitationCodes);
        } finally {
            WayLogger.access("取消查看好友实时坐标：/cancelGetFriendPosition.do,参数：invitationCode：" + invitationCode + "，friendInvitationCodes：" + friendInvitationCodes);
        }
        return serviceResult;
    }

    /**
     * 取消查看组好友实时坐标
     * @param request
     * @param groupId
     * @return
     */
    @RequestMapping(value = "/cancelGetGroupPosition", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> cancelGetGroupPosition(HttpServletRequest request, String groupId){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String invitationCode = (String) request.getAttribute("invitationCode");
        try {
            // 校验token
            if (StringUtils.isBlank(invitationCode)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 取消查看组好友实时坐标
            serviceResult = friendService.cancelGetGroupPosition(invitationCode, groupId);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            serviceResult.setMessage(ServiceResult.ERROR_MSG);
            WayLogger.error(e, "取消查看组好友实时坐标失败," + "请求参数：invitationCode：" + invitationCode + "，groupId：" + groupId);
        } finally {
            WayLogger.access("取消查看组好友实时坐标：/cancelGetGroupPosition.do,参数：invitationCode：" + invitationCode + "，groupId：" + groupId);
        }
        return serviceResult;
    }

    /**
     * 设置好友为退出前可见
     * @param request
     * @param friendInvitationCodes
     * @return
     */
    @RequestMapping(value = "/setFriendsVisibleBeforeExiting", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> setFriendsVisibleBeforeExiting(HttpServletRequest request, String friendInvitationCodes){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String invitationCode = (String) request.getAttribute("invitationCode");
        try {
            // 校验token
            if (StringUtils.isBlank(invitationCode)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(friendInvitationCodes)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            List<String> friendInvitationCodeList = JSON.parseArray(friendInvitationCodes, String.class);
            if(friendInvitationCodeList.size() > 0){
                for(String friendInvitation : friendInvitationCodeList){
                    if (StringUtils.isBlank(friendInvitation)) {
                        return ServiceResult.newFailure("必传参数不能为空");
                    }
                }
            }
            // 设置好友为退出前可见
            serviceResult = friendService.setFriendsVisibleBeforeExiting(invitationCode, friendInvitationCodeList);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            serviceResult.setMessage(ServiceResult.ERROR_MSG);
            WayLogger.error(e, "设置好友为退出前可见失败," + "请求参数：invitationCode：" + invitationCode + "，friendInvitationCodes：" + friendInvitationCodes);
        } finally {
            WayLogger.access("设置好友为退出前可见：/setFriendsVisibleBeforeExiting.do,参数：invitationCode：" + invitationCode + "，friendInvitationCodes：" + friendInvitationCodes);
        }
        return serviceResult;
    }

    /**
     * 查询手机联系人状态
     * @param request
     * @param friendPhoneNo
     * @return
     */
    @RequestMapping(value = "/getPhoneContactStatus", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> getPhoneContactStatus(HttpServletRequest request, String friendPhoneNo){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String invitationCode = (String) request.getAttribute("invitationCode");
        try {
            // 校验token
            if (StringUtils.isBlank(invitationCode)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 查询手机联系人状态
            serviceResult = friendService.getPhoneContactStatus(invitationCode, friendPhoneNo);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            serviceResult.setMessage(ServiceResult.ERROR_MSG);
            WayLogger.error(e, "查询手机联系人状态失败," + "请求参数：invitationCode：" + invitationCode + "，friendPhoneNo：" + friendPhoneNo);
        } finally {
            WayLogger.access("查询手机联系人状态：/getPhoneContactStatus.do,参数：invitationCode：" + invitationCode + "，friendPhoneNo：" + friendPhoneNo);
        }
        return serviceResult;
    }

    /**
     * 查询好友列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/getFriendList", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> getFriendList(HttpServletRequest request){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String invitationCode = (String) request.getAttribute("invitationCode");
        try {
            // 校验token
            if (StringUtils.isBlank(invitationCode)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 查询好友列表
            serviceResult = friendService.getFriendList(invitationCode);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            serviceResult.setMessage(ServiceResult.ERROR_MSG);
            WayLogger.error(e, "查询好友列表失败," + "请求参数：invitationCode：" + invitationCode);
        } finally {
            WayLogger.access("查询好友列表：/getFriendList.do,参数：invitationCode：" + invitationCode);
        }
        return serviceResult;
    }

    /**
     * 申请添加好友
     * @param request
     * @param friendInvitationCode
     * @param applyInfo
     * @return
     */
    @RequestMapping(value = "/applyForAddFriend", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> applyForAddFriend(HttpServletRequest request, String friendInvitationCode, String applyInfo){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String invitationCode = (String) request.getAttribute("invitationCode");
        try {
            // 校验token
            if (StringUtils.isBlank(invitationCode)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(friendInvitationCode)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if(invitationCode.equals(friendInvitationCode)){
                return ServiceResult.newFailure("不能添加自己为好友");
            }
            // 申请添加好友
            serviceResult = friendService.applyForAddFriend(invitationCode, friendInvitationCode, applyInfo);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            serviceResult.setMessage(ServiceResult.ERROR_MSG);
            WayLogger.error(e, "申请添加好友失败," + "请求参数：invitationCode：" + invitationCode);
        } finally {
            WayLogger.access("申请添加好友：/applyForAddFriend.do,参数：invitationCode：" + invitationCode);
        }
        return serviceResult;
    }

    /**
     * 获取被申请好友记录
     * @param request
     * @return
     */
    @RequestMapping(value = "/getApplicationRecordOfFriend", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> getApplicationRecordOfFriend(HttpServletRequest request){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String invitationCode = (String) request.getAttribute("invitationCode");
        try {
            // 校验token
            if (StringUtils.isBlank(invitationCode)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 获取被申请好友记录
            serviceResult = friendService.getApplicationRecordOfFriend(invitationCode);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            serviceResult.setMessage(ServiceResult.ERROR_MSG);
            WayLogger.error(e, "获取被申请好友记录失败," + "请求参数：invitationCode：" + invitationCode);
        } finally {
            WayLogger.access("获取被申请好友记录：/getApplicationRecordOfFriend.do,参数：invitationCode：" + invitationCode);
        }
        return serviceResult;
    }

    /**
     * 同意/拒绝添加好友申请
     * @param request
     * @param friendInvitationCode
     * @param isApprove
     * @param applicationId
     * @return
     */
    @RequestMapping(value = "/agreeToAddFriend", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> agreeToAddFriend(HttpServletRequest request, String friendInvitationCode, String isApprove, String applicationId){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String invitationCode = (String) request.getAttribute("invitationCode");
        try {
            // 校验token
            if (StringUtils.isBlank(invitationCode)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(isApprove)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 同意/拒绝添加好友申请
            serviceResult = friendService.agreeToAddFriend(invitationCode, friendInvitationCode, isApprove, applicationId);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            serviceResult.setMessage(ServiceResult.ERROR_MSG);
            WayLogger.error(e, "同意/拒绝添加好友申请失败," + "请求参数：invitationCode：" + invitationCode);
        } finally {
            WayLogger.access("同意/拒绝添加好友申请：/agreeToAddFriend.do,参数：invitationCode：" + invitationCode);
        }
        return serviceResult;
    }

    /**
     * 查看好友信息
     * @param request
     * @param friendInvitationCode
     * @return
     */
    @RequestMapping(value = "/getFriendInfo", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<FriendsInfoDto> getFriendInfo(HttpServletRequest request, String friendInvitationCode){
        ServiceResult<FriendsInfoDto> serviceResult = ServiceResult.newSuccess();
        String invitationCode = (String) request.getAttribute("invitationCode");
        try {
            // 校验token
            if (StringUtils.isBlank(invitationCode)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(friendInvitationCode)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 查看好友信息
            serviceResult = friendService.getFriendInfo(invitationCode, friendInvitationCode);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            serviceResult.setMessage(ServiceResult.ERROR_MSG);
            WayLogger.error(e, "查看好友信息失败," + "请求参数：invitationCode：" + invitationCode + "，friendInvitationCode：" + friendInvitationCode);
        } finally {
            WayLogger.access("查看好友信息：/getFriendInfo.do,参数：invitationCode：" + invitationCode + "，friendInvitationCode：" + friendInvitationCode);
        }
        return serviceResult;
    }

    /**
     * 修改好友信息
     * @param request
     * @param dto
     * @return
     */
    @RequestMapping(value = "/modifyFriendInfo", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> modifyFriendInfo(HttpServletRequest request, @ModelAttribute FriendsInfoDto dto){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String invitationCode = (String) request.getAttribute("invitationCode");
        try {
            // 校验token
            if (StringUtils.isBlank(invitationCode)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(dto.getFriendInvitationCode())) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(dto.getFriendRemarkName())) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (null == dto.getIsAccreditVisible()) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if(Constants.YES_INT == dto.getIsAccreditVisible()){
                if (StringUtils.isBlank(dto.getAccreditStartTime())) {
                    return ServiceResult.newFailure("必传参数不能为空");
                }
                if (StringUtils.isBlank(dto.getAccreditEndTime())) {
                    return ServiceResult.newFailure("必传参数不能为空");
                }
                if (StringUtils.isBlank(dto.getAccreditWeeks())) {
                    return ServiceResult.newFailure("必传参数不能为空");
                }
            }else{
                if (StringUtils.isBlank(dto.getAccreditStartTime())) {
                    dto.setAccreditStartTime("");
                }
                if (StringUtils.isBlank(dto.getAccreditEndTime())) {
                    dto.setAccreditEndTime("");
                }
                if (StringUtils.isBlank(dto.getAccreditWeeks())) {
                    dto.setAccreditWeeks("");
                }
            }
            // 修改好友信息
            serviceResult = friendService.modifyFriendInfo(invitationCode, dto);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            serviceResult.setMessage(ServiceResult.ERROR_MSG);
            WayLogger.error(e, "修改好友信息失败," + "请求参数：invitationCode：" + invitationCode);
        } finally {
            WayLogger.access("修改好友信息：/modifyFriendInfo.do,参数：invitationCode：" + invitationCode);
        }
        return serviceResult;
    }

    /**
     * 删除好友
     * @param request
     * @param friendInvitationCode
     * @return
     */
    @RequestMapping(value = "/deleteFriend", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> deleteFriend(HttpServletRequest request, String friendInvitationCode){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String invitationCode = (String) request.getAttribute("invitationCode");
        try {
            // 校验token
            if (StringUtils.isBlank(invitationCode)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(friendInvitationCode)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 删除好友
            serviceResult = friendService.deleteFriend(invitationCode, friendInvitationCode);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            serviceResult.setMessage(ServiceResult.ERROR_MSG);
            WayLogger.error(e, "删除好友失败," + "请求参数：invitationCode：" + invitationCode);
        } finally {
            WayLogger.access("删除好友：/deleteFriend.do,参数：invitationCode：" + invitationCode);
        }
        return serviceResult;
    }

    /**
     * 新建组
     * @param request
     * @param groupName
     * @return
     */
    @RequestMapping(value = "/addGroupInfo", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> addGroupInfo(HttpServletRequest request, String groupName){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String invitationCode = (String) request.getAttribute("invitationCode");
        try {
            // 校验token
            if (StringUtils.isBlank(invitationCode)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(groupName)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 新建组
            serviceResult = friendService.addGroupInfo(invitationCode, groupName);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            serviceResult.setMessage(ServiceResult.ERROR_MSG);
            WayLogger.error(e, "新建组失败," + "请求参数：invitationCode：" + invitationCode);
        } finally {
            WayLogger.access("新建组：/addGroupInfo.do,参数：invitationCode：" + invitationCode);
        }
        return serviceResult;
    }

    /**
     * 查看组信息
     * @param request
     * @param groupId
     * @return
     */
    @RequestMapping(value = "/getGroupInfo", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<GroupInfoDto> getGroupInfo(HttpServletRequest request, String groupId){
        ServiceResult<GroupInfoDto> serviceResult = ServiceResult.newSuccess();
        String invitationCode = (String) request.getAttribute("invitationCode");
        try {
            // 校验token
            if (StringUtils.isBlank(invitationCode)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(groupId)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 查看组信息
            serviceResult = friendService.getGroupInfo(invitationCode, groupId);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            serviceResult.setMessage(ServiceResult.ERROR_MSG);
            WayLogger.error(e, "查看组信息失败," + "请求参数：invitationCode：" + invitationCode + "，groupId：" + groupId);
        } finally {
            WayLogger.access("查看组信息：/getGroupInfo.do,参数：invitationCode：" + invitationCode + "，groupId：" + groupId);
        }
        return serviceResult;
    }

    /**
     * 修改组信息
     * @param request
     * @param dto
     * @return
     */
    @RequestMapping(value = "/modifyGroupInfo", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<GroupInfoDto> modifyGroupInfo(HttpServletRequest request, @ModelAttribute GroupInfoDto dto){
        ServiceResult<GroupInfoDto> serviceResult = ServiceResult.newSuccess();
        String invitationCode = (String) request.getAttribute("invitationCode");
        try {
            // 校验token
            if (StringUtils.isBlank(invitationCode)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(dto.getGroupId())) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(dto.getGroupName())) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(dto.getAccreditStartTime())) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(dto.getAccreditEndTime())) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (null == dto.getIsAccreditVisible()) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 修改组信息
            serviceResult = friendService.modifyGroupInfo(invitationCode, dto);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            serviceResult.setMessage(ServiceResult.ERROR_MSG);
            WayLogger.error(e, "修改组信息失败," + "请求参数：invitationCode：" + invitationCode + "，GroupInfoDto：" + dto);
        } finally {
            WayLogger.access("修改组信息：/modifyGroupInfo.do,参数：invitationCode：" + invitationCode + "，GroupInfoDto：" + dto);
        }
        return serviceResult;
    }

    /**
     * 删除组信息
     * @param request
     * @param groupId
     * @return
     */
    @RequestMapping(value = "/deleteGroupInfo", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<GroupInfoDto> deleteGroupInfo(HttpServletRequest request, String groupId){
        ServiceResult<GroupInfoDto> serviceResult = ServiceResult.newSuccess();
        String invitationCode = (String) request.getAttribute("invitationCode");
        try {
            // 校验token
            if (StringUtils.isBlank(invitationCode)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(groupId)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 删除组信息
            serviceResult = friendService.deleteGroupInfo(invitationCode, groupId);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            serviceResult.setMessage(ServiceResult.ERROR_MSG);
            WayLogger.error(e, "删除组信息失败," + "请求参数：invitationCode：" + invitationCode + "，groupId：" + groupId);
        } finally {
            WayLogger.access("删除组信息：/deleteGroupInfo.do,参数：invitationCode：" + invitationCode + "，groupId：" + groupId);
        }
        return serviceResult;
    }

    /**
     * 将好友添加到分组
     * @param request
     * @param groupId
     * @return
     */
    @RequestMapping(value = "/moveFriendToGroup", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> moveFriendToGroup(HttpServletRequest request, String groupId, String friendInvitationCodes){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String invitationCode = (String) request.getAttribute("invitationCode");
        try {
            // 校验token
            if (StringUtils.isBlank(invitationCode)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(groupId)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(friendInvitationCodes)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 将好友添加到分组
            serviceResult = friendService.moveFriendToGroup(invitationCode, groupId, friendInvitationCodes);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            serviceResult.setMessage(ServiceResult.ERROR_MSG);
            WayLogger.error(e, "将好友添加到分组失败," + "请求参数：invitationCode：" + invitationCode + "，groupId：" + groupId + "，friendInvitationCodes：" + friendInvitationCodes);
        } finally {
            WayLogger.access("将好友添加到分组：/moveFriendToGroup.do,参数：invitationCode：" + invitationCode + "，groupId：" + groupId + "，friendInvitationCodes：" + friendInvitationCodes);
        }
        return serviceResult;
    }

    /**
     * 将好友从分组中移除
     * @param request
     * @param groupId
     * @param friendInvitationCodes
     * @return
     */
    @RequestMapping(value = "/removeFriendFromGroup", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> removeFriendFromGroup(HttpServletRequest request, String groupId, String friendInvitationCodes){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String invitationCode = (String) request.getAttribute("invitationCode");
        try {
            // 校验token
            if (StringUtils.isBlank(invitationCode)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(groupId)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(friendInvitationCodes)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 将好友从分组中移除
            serviceResult = friendService.removeFriendFromGroup(invitationCode, groupId, friendInvitationCodes);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            serviceResult.setMessage(ServiceResult.ERROR_MSG);
            WayLogger.error(e, "将好友从分组中移除失败," + "请求参数：invitationCode：" + invitationCode + "，groupId：" + groupId + "，friendInvitationCodes：" + friendInvitationCodes);
        } finally {
            WayLogger.access("将好友从分组中移除：/removeFriendFromGroup.do,参数：invitationCode：" + invitationCode + "，groupId：" + groupId + "，friendInvitationCodes：" + friendInvitationCodes);
        }
        return serviceResult;
    }
}
