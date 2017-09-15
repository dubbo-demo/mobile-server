package com.way.mobile.service.downloadFile;

/**
 * 功能描述：文件下载Service
 *
 * @Author：xinpei.xu
 * @Date：2017/08/22 20:43
 */
public interface DownloadFileService {

    /**
     * 根据图片ID下载图片信息
     * @param fileId
     * @return
     */
    byte[] getFileInfoByFileId(String fileId);
}
