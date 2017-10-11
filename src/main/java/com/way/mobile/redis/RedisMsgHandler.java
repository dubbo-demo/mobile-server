package com.way.mobile.redis;

import com.way.common.constant.RedisConstants;
import com.way.common.redis.JedisAction;
import com.way.common.redis.utils.NoShardedRedisCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;

/**
 * 客户端版本服务<br>
 * 〈功能详细描述〉
 *
 * @author xinpei.xu
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@DependsOn({ "base-springContextHolder"})
@Service
public class RedisMsgHandler {

    @Autowired
    NoShardedRedisCacheUtil noShardedRedisService;

    @Autowired
    InvokeService invokeService;

    @PostConstruct
    public void init() {
        subscribe();
    }

    /**
     * 执行订阅方法
     */
    private void subscribe() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final RedisMsgListener redisMsgListener = new RedisMsgListener();
                redisMsgListener.setInvokeService(invokeService);

                noShardedRedisService.getJedisClient().execute(new JedisAction<Object>() {
                    @Override
                    public Object doAction(Jedis jedis) {
                        jedis.subscribe(redisMsgListener, RedisConstants.INVOKE_CHANNEL);
                        return null;
                    }
                });
            }
        }).start();
    }
}
