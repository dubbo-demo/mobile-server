package com.way.mobile.controller.uploadFile;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.way.common.log.WayLogger;
import com.way.common.result.ServiceResult;
import com.way.mobile.common.util.TokenJedisUtils;
import com.way.mobile.service.uploadFile.UploadFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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

    /**
     * 头像上传
     * @param request
     * @return
     */
    @RequestMapping(value = "/uploadHeadPic", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> uploadHeadPic(MultipartFile file, HttpServletRequest request){
        String memberId = "";
        String fileId = "";
        try {
            ServiceResult result = ServiceResult.newSuccess();
            // 获取用户手机号
            memberId = TokenJedisUtils.getMemberIdByToken(request.getParameter("token"));
            // 校验参数
            if (null == memberId || null == file ) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 头像上传
            fileId = uploadFileService.uploadHeadPic(memberId, file);
            if(StringUtils.isBlank(fileId)){
                return ServiceResult.newFailure();
            }
            Map<String, String> resMap = new HashMap<String, String>();
            resMap.put("fileId", fileId);
            result.setData(resMap);
            return result;
        } catch (Exception e) {
            WayLogger.error(e, "头像上传," + "请求参数：" + memberId);
            return ServiceResult.newFailure();
        } finally {
            WayLogger.access("头像上传：/uploadHeadPic.do,参数：" + memberId + ",文件id：" + fileId);
        }
    }

}
