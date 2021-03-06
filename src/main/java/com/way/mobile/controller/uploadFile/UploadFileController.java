package com.way.mobile.controller.uploadFile;

import com.way.common.log.WayLogger;
import com.way.common.result.ServiceResult;
import com.way.mobile.service.member.MemberService;
import com.way.mobile.service.uploadFile.UploadFileService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: UploadFileController
 * @Description: 文件上传Controller
 * @author xinpei.xu
 * @date 2017/08/21 21:01
 *
 */
@Controller
public class UploadFileController {

    @Autowired
    private UploadFileService uploadFileService;

    @Autowired
    private MemberService memberService;

    /**
     * 头像上传
     * @param request
     * @return
     */
    @RequestMapping(value = "/uploadHeadPic", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> uploadHeadPic(@ModelAttribute MultipartFile file, HttpServletRequest request){
        String invitationCode = (String) request.getAttribute("invitationCode");
        String fileId = "";
        try {
            ServiceResult result = ServiceResult.newSuccess();
            // 校验参数
            if (StringUtils.isBlank(invitationCode) || null == file ) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
//            // 根据手机号查用户邀请码
//            ServiceResult<MemberDto> memberDto = memberService.getMemberInfo(invitationCode);

            // 头像上传
            fileId = uploadFileService.uploadHeadPic(invitationCode, file);
            if(StringUtils.isBlank(fileId)){
                return ServiceResult.newFailure();
            }
            Map<String, String> resMap = new HashMap<String, String>();
            resMap.put("fileId", fileId);
            result.setData(resMap);
            return result;
        } catch (Exception e) {
            WayLogger.error(e, "头像上传," + "请求参数：" + invitationCode);
            return ServiceResult.newFailure();
        } finally {
            WayLogger.access("头像上传：/uploadHeadPic.do,参数：" + invitationCode + ",文件id：" + fileId);
        }
    }

}
