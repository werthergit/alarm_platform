package org.werther.ap.redis.alarm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class MetricsNotify {

    @Autowired
    private RulesWatcher rulesWatcher;

    @Autowired
    private RunningRule runningRule;

    public void notify(Metrics metrics){
        String metricName = metrics.getMetricName();
        //1、获得规则
        AlarmRule alarmRule = rulesWatcher.findRunningRule(metricName);
        //log.info("runningRules size：{}", runningRules.size());
        if (alarmRule == null) {
            return;
        }
        runningRule.in(alarmRule, metrics);
    }


}
