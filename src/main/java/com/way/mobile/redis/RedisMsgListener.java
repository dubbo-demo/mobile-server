package com.way.mobile.redis;

import com.alibaba.fastjson.JSON;
import com.way.common.constant.RedisConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPubSub;

/**
 * 客户端版本服务<br>
 * 〈功能详细描述〉
 *
 * @author xinpei.xu
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class RedisMsgListener extends JedisPubSub {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisMsgListener.class);

    private InvokeService invokeService;

    @Override
    public void onMessage(String channel, String message) {
        LOGGER.info("pubSub onMessage, channel=" + channel + ", message=" + message);
        if (RedisConstants.INVOKE_CHANNEL.equals(channel)) {
            try {
                invokeService.execute(JSON.parseObject(message, InvokeMsg.class));
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        // pubSub onPMessage
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        // pubSub onSubscribe
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        // pubSub onUnsubscribe
    }

    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {
        // pubSub onPUnsubscribe
    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
        // pubSub onPSubscribe
    }

    public void setInvokeService(InvokeService invokeService) {
        this.invokeService = invokeService;
    }
}
