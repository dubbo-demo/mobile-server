package com.way.mobile.service.uploadFile.impl;

import com.way.common.log.WayLogger;
import com.way.common.util.BeanUtils;
import com.way.mobile.service.uploadFile.UploadFileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @ClassName: UploadFileServiceImpl
 * @Description: 文件上传ServiceImpl
 * @author xinpei.xu
 * @date 2017/08/21 21:19
 *
 */
@Service
public class UploadFileServiceImpl implements UploadFileService {



    /**
     * 头像上传
     * @param memberId
     * @param file
     * @return
     */
    @Override
    public String uploadHeadPic(String memberId, MultipartFile file) {
        // UUID作为key
        String rowKey = "";
        try {
            rowKey = BeanUtils.getUUID();

            file.getBytes();
            // 将文件存入大数据

            // 会员id
//            dto.setMemberId(Long.valueOf(memberId));
//            // 大数据附件id
//            dto.setFileStr(rowKey);
//            String originalFilename = file.getOriginalFilename();
//            String[] fileItems = originalFilename.split("\\.");
//            // 文件名称
//            dto.setFileName(originalFilename);
//            if (fileItems.length >= 2) {
//                // 文件后缀
//                dto.setFileFormart(fileItems[fileItems.length - 1]);
//            }
//            // 文件大小
//            Long fileSize = file.getSize();
//            dto.setFileSize(fileSize);
//            dto.setCreateTime(new Date());
//            // 保存文件信息到APP附件信息表
//            jkAppFileInfoService.insert(dto);
            return rowKey;
        } catch (Exception e) {
            WayLogger.error(e, "文件上传异常,入参:{}", memberId);
            return null;
        }
    }
}
