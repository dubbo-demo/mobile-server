package com.way.mobile.service.uploadFile.impl;

import com.way.base.file.dto.FileInfoDto;
import com.way.base.file.service.FileInfoService;
import com.way.common.log.WayLogger;
import com.way.common.util.BeanUtils;
import com.way.member.member.service.MemberInfoService;
import com.way.mobile.service.uploadFile.UploadFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * @ClassName: UploadFileServiceImpl
 * @Description: 文件上传ServiceImpl
 * @author xinpei.xu
 * @date 2017/08/21 21:19
 *
 */
@Service
public class UploadFileServiceImpl implements UploadFileService {

    @Autowired
    private FileInfoService fileInfoService;

    @Autowired
    private MemberInfoService memberInfoService;

    /**
     * 头像上传
     * @param phoneNo
     * @param file
     * @return
     */
    @Override
    public String uploadHeadPic(String phoneNo, MultipartFile file) {
        // UUID作为key
        String fileUuid = "";
        try {
            // 根据手机号查出用户头像是否存在
            FileInfoDto dto = fileInfoService.getFileInfoByPhoneNo(phoneNo);
            if(null == dto){
                dto = new FileInfoDto();
                dto.setPhoneNo(phoneNo);// 手机号
                String originalFilename = file.getOriginalFilename();
                String[] fileItems = originalFilename.split("\\.");
                // 文件名称
                dto.setFileName(originalFilename);// 文件名
                if (fileItems.length >= 2) {
                    // 文件后缀
                    dto.setFileFormat(fileItems[fileItems.length - 1]);
                }
                dto.setFileSize(file.getSize());// 文件大小
                dto.setFileStream(file.getBytes());// 文件二进制流
                fileUuid = BeanUtils.getUUID();
                dto.setFileUuid(fileUuid);// 文件UUID
                dto.setCreateTime(new Date());// 创建时间
                dto.setModifyTime(new Date());// 修改时间
                // 保存用户头像信息
                fileInfoService.saveFileInfo(dto);
                // 根据手机号更新用户头像id
                memberInfoService.updateHeadPicIdByPhoneNo(phoneNo, fileUuid);
            }else{
                String originalFilename = file.getOriginalFilename();
                String[] fileItems = originalFilename.split("\\.");
                dto.setPhoneNo(phoneNo);// 手机号
                // 文件名称
                dto.setFileName(originalFilename);// 文件名
                if (fileItems.length >= 2) {
                    // 文件后缀
                    dto.setFileFormat(fileItems[fileItems.length - 1]);
                }
                dto.setFileSize(file.getSize());// 文件大小
                dto.setFileStream(file.getBytes());// 文件二进制流
                fileUuid = BeanUtils.getUUID();
                dto.setFileUuid(fileUuid);// 文件UUID
                dto.setModifyTime(new Date());// 修改时间
                // 根据手机号更新用户头像
                fileInfoService.updateFileInfo(dto);
                // 根据手机号更新用户头像id
                memberInfoService.updateHeadPicIdByPhoneNo(phoneNo, fileUuid);
            }
            return fileUuid;
        } catch (Exception e) {
            WayLogger.error(e, "文件上传异常,入参:{}", phoneNo);
            return null;
        }
    }
}
