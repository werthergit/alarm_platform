package org.werther.ap.redis.alarm;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.time.LocalTime;

@Slf4j
@SpringBootTest
public class MetricsNotifyTest {

    @Autowired
    private  MetricsNotify metricsNotify;

    @Test
    public void redisLuaLimiterTests2() throws InterruptedException, IOException {
        //RulesWatcher rulesWatcher = new RulesWatcher();
        //FeishuHookCallback feishuHookCallback = new FeishuHookCallback();
        //MetricsNotify metricsNotify = new MetricsNotify(rulesWatcher, feishuHookCallback);

        for (int i = 0; i < 15; i++) {
            Thread.sleep(3*1_000);
            log.info("---i:{}", i);
            Metrics metrics = new Metrics();
            metrics.setValue(998+i);
            metrics.setMetricName("m01");
            metricsNotify.notify(metrics);
        }

    }
}
