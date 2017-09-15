package com.way.mobile.service.downloadFile.impl;

import com.way.base.file.dto.FileInfoDto;
import com.way.base.file.service.FileInfoService;
import com.way.mobile.service.downloadFile.DownloadFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 功能描述：文件下载ServiceImpl
 *
 * @Author：xinpei.xu
 * @Date：2017年09月15日 16:28
 */
@Service
public class DownloadFileServiceImpl implements DownloadFileService {

    @Autowired
    private FileInfoService fileInfoService;

    /**
     * 根据图片ID下载图片信息
     * @param fileId
     * @return
     */
    @Override
    public byte[] getFileInfoByFileId(String fileId) {
        // 根据图片ID下载图片信息
        FileInfoDto dto = fileInfoService.getFileInfoByFileId(fileId);
        if(null != dto){
            return dto.getFileStream();
        }
        return new byte[0];
    }
}
