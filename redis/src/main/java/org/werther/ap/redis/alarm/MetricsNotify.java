package org.werther.ap.redis.alarm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class MetricsNotify {

    @Autowired
    private RulesWatcher rulesWatcher;

    @Autowired
    private AlarmCallback alarmCallback;

//    public MetricsNotify(RulesWatcher rulesWatcher, AlarmCallback alarmCallback){
//        this.rulesWatcher = rulesWatcher;
//        this.alarmCallback = alarmCallback;
//    }

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    private String KEY_PREFIX = "lcp_alarm_";

    public void notify(Metrics metrics){

        String metricName = metrics.getMetricName();
        //1、获得规则
        AlarmRule alarmRule = rulesWatcher.findRunningRule(metricName);
        //log.info("runningRules size：{}", runningRules.size());
        if (alarmRule == null) {
            return;
        }

        //2、加入window
        int period = alarmRule.getPeriod();
        //String threshold = alarmRule.getThreshold();
        int count = alarmRule.getCount();
        boolean flag = add(metricName, period, count);
        //1、false ,doAlarm
        if( !flag ){
            alarmCallback.doAlarm();
        }
    }

    private boolean add(String key,int period, int count){
        long now = System.currentTimeMillis();
        key = KEY_PREFIX + key;
        String oldest = String.valueOf(now - period*1_000);
        String score = String.valueOf(now);
        String scoreValue = score;
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Long.class);
        //lua文件存放在resources目录下
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("alarm.lua")));
        Long x = stringRedisTemplate.execute(redisScript, Arrays.asList(key), oldest,
                score, String.valueOf(count), scoreValue);
        // System.out.println(x);
        return x == 1;
    }

}
