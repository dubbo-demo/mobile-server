package com.way.mobile.service.uploadFile;

import org.springframework.web.multipart.MultipartFile;

/**
 * @ClassName: UploadFileService
 * @Description: 文件上传Service
 * @author xinpei.xu
 * @date 2017/08/21 21:14
 *
 */
public interface UploadFileService {

    /**
     * 头像上传
     * @param invitationCode
     * @param file
     * @return
     */
    String uploadHeadPic(String invitationCode, MultipartFile file);
}
