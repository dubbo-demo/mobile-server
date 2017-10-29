package com.way.mobile.controller.downloadFile;

import com.way.common.log.WayLogger;
import com.way.common.util.IpUtil;
import com.way.mobile.service.downloadFile.DownloadFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;

/**
 * @ClassName: DownloadFileController
 * @Description: 文件下载Controller
 * @author xinpei.xu
 * @date 2017/08/22 20:26
 *
 */
@Controller
public class DownloadFileController {

    @Autowired
    private DownloadFileService downloadFileService;

    /**
     * 下载图片
     * 
     * @param resp
     * @param request
     * @param fileId
     */
    @RequestMapping("/downLoadPic")
    @ResponseBody
    public void downLoadPic(HttpServletResponse resp, HttpServletRequest request, String fileId) {
        OutputStream stream = null;
        try {
            // 根据图片ID下载图片信息
            byte[] data = downloadFileService.getFileInfoByFileId(fileId);
            resp.reset();
            resp.setContentType("application/octet-stream; charset=utf-8");
            // 设置Content-Disposition
            resp.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("abc", "UTF-8"));
            stream = resp.getOutputStream();
            stream.write(data);
            stream.flush();
            stream.close();
        } catch (Exception e) {
            WayLogger.error("下载图片失败");
        } finally {
            WayLogger.access(IpUtil.getIpAddr(request) + "下载图片：/downLoadPic.do" + ",参数：" + fileId);
		}
    }
}
