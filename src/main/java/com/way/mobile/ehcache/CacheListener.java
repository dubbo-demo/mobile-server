package com.way.mobile.ehcache;

import com.way.mobile.ehcache.service.VersionConfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 客户端版本服务<br>
 * 〈功能详细描述〉
 *
 * @author xinpei.xu
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class CacheListener implements ServletContextListener {

    /**
     * 日志实例
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOGGER.info("Enter into method contextInitialized");
        try {
            ServletContext servletContext = sce.getServletContext();
            ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext);

            VersionConfService versionConfService = (VersionConfService) ctx.getBean("versionConfServiceImpl");
            // 发布订阅重载EH缓存
            versionConfService.refreshVersionConf();
        } catch (Exception e) {
            LOGGER.error("contextInitialized invokePublish error, e={}", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Auto-generated method stub
    }
}
