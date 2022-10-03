package org.werther.ap.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Arrays;

@SpringBootTest
class RedisLuaLimiterByZset {

    private String KEY_PREFIX = "limiter_";
    private String QPS = "4";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void redisLuaLimiterTests() throws InterruptedException, IOException {
        for (int i = 0; i < 15; i++) {
            Thread.sleep(180);
            System.out.println(LocalTime.now() + " " + acquire("user1"));
        }
    }

    @Test
    public void redisLuaLimiterTests2() throws InterruptedException, IOException {
//        for (int i = 0; i < 15; i++) {
//            Thread.sleep(180);
//            System.out.println(LocalTime.now() + " " + acquire("user1", 1l, "3"));
//        }
        String key = "user2";
        int wl = 10;
        System.out.println(LocalTime.now() + " " + acquire(key, wl, "3"));
        Thread.sleep(2000);
        System.out.println(LocalTime.now() + " " + acquire(key, wl, "3"));

        Thread.sleep(3000);
        System.out.println(LocalTime.now() + " " + acquire(key, wl, "3"));
       // System.out.println(LocalTime.now() + " " + acquire(key, 110, "3"));
        Thread.sleep(10*1_000);
        System.out.println(LocalTime.now() + " " + acquire(key, wl, "3"));

    }

    /**
     * 计数器限流
     *
     * @param key
     * @return
     */
    public boolean acquire(String key) {
        long now = System.currentTimeMillis();
        key = KEY_PREFIX + key;
        String oldest = String.valueOf(now - 1_000);
        String score = String.valueOf(now);
        String scoreValue = score;
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Long.class);
        //lua文件存放在resources目录下
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("limiter2.lua")));
        return stringRedisTemplate.execute(redisScript, Arrays.asList(key), oldest, score, QPS, scoreValue) == 1;
    }

    /**
     * 计数器限流
     * period
     * count
     * @param key
     * @return
     */
    public boolean acquire(String key, int windowLength,  String threshold) {
        long now = System.currentTimeMillis();
        key = KEY_PREFIX + key;
        String oldest = String.valueOf(now - windowLength*1_000);
        String score = String.valueOf(now);
        String scoreValue = score;
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Long.class);
        //lua文件存放在resources目录下
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("limiter2.lua")));
        Long x = stringRedisTemplate.execute(redisScript, Arrays.asList(key), oldest, score, threshold, scoreValue);
        System.out.println(x);
        return x == 1;
    }
}
