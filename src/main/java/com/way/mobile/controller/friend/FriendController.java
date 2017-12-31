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
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 获取首页好友以及组信息
            serviceResult = friendService.getFriendsAndGroups(phoneNo);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "获取首页好友以及组信息失败," + "请求参数：" + phoneNo);
        } finally {
            WayLogger.access("获取首页好友以及组信息：/getFriendsAndGroups.do,参数：" + phoneNo);
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
    public ServiceResult<Object> cancelGetFriendPosition(HttpServletRequest request, String friendPhoneNos){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(friendPhoneNos)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            List<String> friendPhoneNoList = JSON.parseArray(friendPhoneNos, String.class);
            for(String friendPhoneNo : friendPhoneNoList){
                if (StringUtils.isBlank(friendPhoneNo)) {
                    return ServiceResult.newFailure("必传参数不能为空");
                }
            }
            // 取消查看好友实时坐标
            serviceResult = friendService.cancelGetFriendPosition(phoneNo, friendPhoneNoList);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "取消查看好友实时坐标失败," + "请求参数：phoneNo：" + phoneNo + "，friendPhoneNos：" + friendPhoneNos);
        } finally {
            WayLogger.access("取消查看好友实时坐标：/cancelGetFriendPosition.do,参数：phoneNo：" + phoneNo + "，friendPhoneNos：" + friendPhoneNos);
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
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 取消查看组好友实时坐标
            serviceResult = friendService.cancelGetGroupPosition(phoneNo, groupId);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "取消查看组好友实时坐标失败," + "请求参数：phoneNo：" + phoneNo + "，groupId：" + groupId);
        } finally {
            WayLogger.access("取消查看组好友实时坐标：/cancelGetFriendPosition.do,参数：phoneNo：" + phoneNo + "，groupId：" + groupId);
        }
        return serviceResult;
    }

    /**
     * 设置好友为退出前可见
     * @param request
     * @param friendPhoneNos
     * @return
     */
    @RequestMapping(value = "/setFriendsVisibleBeforeExiting", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> setFriendsVisibleBeforeExiting(HttpServletRequest request, String friendPhoneNos){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(friendPhoneNos)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            List<String> friendPhoneNoList = JSON.parseArray(friendPhoneNos, String.class);
            if(friendPhoneNoList.size() > 0){
                for(String friendPhoneNo : friendPhoneNoList){
                    if (StringUtils.isBlank(friendPhoneNo)) {
                        return ServiceResult.newFailure("必传参数不能为空");
                    }
                }
            }
            // 设置好友为退出前可见
            serviceResult = friendService.setFriendsVisibleBeforeExiting(phoneNo, friendPhoneNoList);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "设置好友为退出前可见失败," + "请求参数：phoneNo：" + phoneNo + "，friendPhoneNos：" + friendPhoneNos);
        } finally {
            WayLogger.access("设置好友为退出前可见：/setFriendsVisibleBeforeExiting.do,参数：phoneNo：" + phoneNo + "，friendPhoneNos：" + friendPhoneNos);
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
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 查询手机联系人状态
            serviceResult = friendService.getPhoneContactStatus(phoneNo, friendPhoneNo);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "查询手机联系人状态失败," + "请求参数：phoneNo：" + phoneNo + "，friendPhoneNo：" + friendPhoneNo);
        } finally {
            WayLogger.access("查询手机联系人状态：/getPhoneContactStatus.do,参数：phoneNo：" + phoneNo + "，friendPhoneNo：" + friendPhoneNo);
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
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 查询好友列表
            serviceResult = friendService.getFriendList(phoneNo);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "查询好友列表失败," + "请求参数：phoneNo：" + phoneNo);
        } finally {
            WayLogger.access("查询好友列表：/getFriendList.do,参数：phoneNo：" + phoneNo);
        }
        return serviceResult;
    }

    /**
     * 申请添加好友
     * @param request
     * @param friendPhoneNo
     * @param applyInfo
     * @return
     */
    @RequestMapping(value = "/applyForAddFriend", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> applyForAddFriend(HttpServletRequest request, String friendPhoneNo, String applyInfo){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(friendPhoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 申请添加好友
            serviceResult = friendService.applyForAddFriend(phoneNo, friendPhoneNo, applyInfo);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "申请添加好友失败," + "请求参数：phoneNo：" + phoneNo);
        } finally {
            WayLogger.access("申请添加好友：/applyForAddFriend.do,参数：phoneNo：" + phoneNo);
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
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 获取被申请好友记录
            serviceResult = friendService.getApplicationRecordOfFriend(phoneNo);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "获取被申请好友记录失败," + "请求参数：phoneNo：" + phoneNo);
        } finally {
            WayLogger.access("获取被申请好友记录：/getApplicationRecordOfFriend.do,参数：phoneNo：" + phoneNo);
        }
        return serviceResult;
    }

    /**
     * 同意/拒绝添加好友申请
     * @param request
     * @param friendPhoneNo
     * @param isApprove
     * @param applicationId
     * @return
     */
    @RequestMapping(value = "/agreeToAddFriend", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> agreeToAddFriend(HttpServletRequest request, String friendPhoneNo, String isApprove, String applicationId){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(isApprove)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 同意/拒绝添加好友申请
            serviceResult = friendService.agreeToAddFriend(phoneNo, friendPhoneNo, isApprove, applicationId);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "同意/拒绝添加好友申请失败," + "请求参数：phoneNo：" + phoneNo);
        } finally {
            WayLogger.access("同意/拒绝添加好友申请：/agreeToAddFriend.do,参数：phoneNo：" + phoneNo);
        }
        return serviceResult;
    }

    /**
     * 查看好友信息
     * @param request
     * @param friendPhoneNo
     * @return
     */
    @RequestMapping(value = "/getFriendInfo", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<FriendsInfoDto> getFriendInfo(HttpServletRequest request, String friendPhoneNo){
        ServiceResult<FriendsInfoDto> serviceResult = ServiceResult.newSuccess();
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(friendPhoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 查看好友信息
            serviceResult = friendService.getFriendInfo(phoneNo, friendPhoneNo);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "查看好友信息失败," + "请求参数：phoneNo：" + phoneNo + "，friendPhoneNo：" + friendPhoneNo);
        } finally {
            WayLogger.access("查看好友信息：/getFriendInfo.do,参数：phoneNo：" + phoneNo + "，friendPhoneNo：" + friendPhoneNo);
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
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(dto.getFriendPhoneNo())) {
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
            serviceResult = friendService.modifyFriendInfo(phoneNo, dto);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "修改好友信息失败," + "请求参数：phoneNo：" + phoneNo);
        } finally {
            WayLogger.access("修改好友信息：/modifyFriendInfo.do,参数：phoneNo：" + phoneNo);
        }
        return serviceResult;
    }

    /**
     * 删除好友
     * @param request
     * @param friendPhoneNo
     * @return
     */
    @RequestMapping(value = "/deleteFriend", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> deleteFriend(HttpServletRequest request, String friendPhoneNo){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(friendPhoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 删除好友
            serviceResult = friendService.deleteFriend(phoneNo, friendPhoneNo);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "删除好友失败," + "请求参数：phoneNo：" + phoneNo);
        } finally {
            WayLogger.access("删除好友：/deleteFriend.do,参数：phoneNo：" + phoneNo);
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
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(groupName)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 新建组
            serviceResult = friendService.addGroupInfo(phoneNo, groupName);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "新建组失败," + "请求参数：phoneNo：" + phoneNo);
        } finally {
            WayLogger.access("新建组：/addGroupInfo.do,参数：phoneNo：" + phoneNo);
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
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(groupId)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 查看组信息
            serviceResult = friendService.getGroupInfo(phoneNo, groupId);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "查看组信息失败," + "请求参数：phoneNo：" + phoneNo + "，groupId：" + groupId);
        } finally {
            WayLogger.access("查看组信息：/getGroupInfo.do,参数：phoneNo：" + phoneNo + "，groupId：" + groupId);
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
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
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
            serviceResult = friendService.modifyGroupInfo(phoneNo, dto);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "修改组信息失败," + "请求参数：phoneNo：" + phoneNo + "，GroupInfoDto：" + dto);
        } finally {
            WayLogger.access("修改组信息：/modifyGroupInfo.do,参数：phoneNo：" + phoneNo + "，GroupInfoDto：" + dto);
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
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(groupId)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 删除组信息
            serviceResult = friendService.deleteGroupInfo(phoneNo, groupId);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "删除组信息失败," + "请求参数：phoneNo：" + phoneNo + "，groupId：" + groupId);
        } finally {
            WayLogger.access("删除组信息：/deleteGroupInfo.do,参数：phoneNo：" + phoneNo + "，groupId：" + groupId);
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
    public ServiceResult<Object> moveFriendToGroup(HttpServletRequest request, String groupId, String friendPhoneNos){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(groupId)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(friendPhoneNos)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 将好友添加到分组
            serviceResult = friendService.moveFriendToGroup(phoneNo, groupId, friendPhoneNos);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "将好友添加到分组失败," + "请求参数：phoneNo：" + phoneNo + "，groupId：" + groupId + "，friendPhoneNos：" + friendPhoneNos);
        } finally {
            WayLogger.access("将好友添加到分组：/moveFriendToGroup.do,参数：phoneNo：" + phoneNo + "，groupId：" + groupId + "，friendPhoneNos：" + friendPhoneNos);
        }
        return serviceResult;
    }

    /**
     * 将好友从分组中移除
     * @param request
     * @param groupId
     * @param friendPhoneNos
     * @return
     */
    @RequestMapping(value = "/removeFriendFromGroup", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> removeFriendFromGroup(HttpServletRequest request, String groupId, String friendPhoneNos){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(groupId)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(friendPhoneNos)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 将好友从分组中移除
            serviceResult = friendService.removeFriendFromGroup(phoneNo, groupId, friendPhoneNos);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "将好友从分组中移除失败," + "请求参数：phoneNo：" + phoneNo + "，groupId：" + groupId + "，friendPhoneNos：" + friendPhoneNos);
        } finally {
            WayLogger.access("将好友从分组中移除：/removeFriendFromGroup.do,参数：phoneNo：" + phoneNo + "，groupId：" + groupId + "，friendPhoneNos：" + friendPhoneNos);
        }
        return serviceResult;
    }
}
