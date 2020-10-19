package com.cameronsino.toolkit.redis;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.repository.support.RedisRepositoryFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
@ConditionalOnClass(value = {RedisRepositoryFactory.class})
public class RedisLock {

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    private void init() {
        REDIS_TEMPLATE = this.redisTemplate;
    }


    private static RedisTemplate<String, Object> REDIS_TEMPLATE;
    /**
     * 解锁脚本，原子操作
     */
    private static final String unlockScript = "if redis.call('get',KEYS[1]) == ARGV[1] then" +
            "   return redis.call('del',KEYS[1]) " +
            "else" +
            "   return 0 " +
            "end";

    private static final Charset CHARSET = Charset.forName("UTF-8");
    private static final int SLEEP = 50;

    /**
     * 加锁，有阻塞
     *
     * @param name   锁的名字
     * @param expire 锁的时间
     */
    public static String lock(String name, long expire) {
        return lock(name, expire, 60_000);
    }

    /**
     * 加锁，有阻塞
     *
     * @param name     锁的名字
     * @param expire   锁的时间
     * @param supplier 执行逻辑
     */
    public static <T> T lock(String name, long expire, Supplier<T> supplier) {
        String token = null;
        try {
            token = RedisLock.lock(name, expire);
            if (Objects.nonNull(token)) {
                return supplier.get();
            }
            return null;
        } finally {
            if (Objects.nonNull(token)) {
                RedisLock.unlock(name, token);
            }
        }
    }

    /**
     * 加锁，有阻塞
     *
     * @param name    锁的名字
     * @param expire  锁的时间
     * @param timeout 等待锁超时的时间
     */
    public static String lock(String name, long expire, long timeout) {
        long startTime = System.currentTimeMillis();
        String token;
        do {
            if (Objects.isNull(token = notWaitLock(name, expire))) {
                if ((System.currentTimeMillis() - startTime) >= timeout) break;
                try {
                    Thread.sleep(SLEEP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        } while (Objects.isNull(token));
        return token;
    }

    /**
     * 加锁，无阻塞
     *
     * @param name   锁的名字
     * @param expire 过期时间
     */
    public static String notWaitLock(String name, long expire) {
        return notWaitLock(UUID.randomUUID().toString(), name, expire);
    }

    /**
     * 加锁，无阻塞
     *
     * @param name   锁的名字
     * @param expire 过期时间
     */
    public static String notWaitLock(String token, String name, long expire) {
        Assert.state(StringUtils.hasText(token), "token 不能为空");
        Assert.state(StringUtils.hasText(name), "name 不能为空");
        Assert.state(expire > 0, "expire 不能小于0");
        RedisConnectionFactory factory = REDIS_TEMPLATE.getRequiredConnectionFactory();
        RedisConnection conn = factory.getConnection();
        try {
            Boolean result = conn.set(name.getBytes(CHARSET), token.getBytes(CHARSET),
                    Expiration.from(expire, TimeUnit.MILLISECONDS), RedisStringCommands.SetOption.SET_IF_ABSENT);
            if (result != null && result)
                return token;
        } finally {
            RedisConnectionUtils.releaseConnection(conn, factory);
        }
        return null;
    }

    /**
     * 加锁，无阻塞
     *
     * @param name  锁的名字
     * @param token 锁的token
     */
    @SuppressWarnings("UnusedReturnValue")
    public static boolean unlock(String name, String token) {
        Assert.state(StringUtils.hasText(token), "token 不能为空");
        Assert.state(StringUtils.hasText(name), "name 不能为空");
        byte[][] keysAndArgs = new byte[2][];
        keysAndArgs[0] = name.getBytes(CHARSET);
        keysAndArgs[1] = token.getBytes(CHARSET);
        RedisConnectionFactory factory = REDIS_TEMPLATE.getRequiredConnectionFactory();
        RedisConnection conn = factory.getConnection();
        try {
            Long result = conn.scriptingCommands().eval(unlockScript.getBytes(CHARSET), ReturnType.INTEGER, 1, keysAndArgs);
            if (result != null && result > 0) {
                return true;
            }
        } finally {
            RedisConnectionUtils.releaseConnection(conn, factory);
        }
        return false;
    }
}
