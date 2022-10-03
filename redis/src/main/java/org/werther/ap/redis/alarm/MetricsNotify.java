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
        //静默时长
        int silencePeriod = alarmRule.getSilencePeriod();

        boolean isMatch = add(metricName, period, count, silencePeriod);
        //1、true ,doAlarm
        if( isMatch ){
            alarmCallback.doAlarm();
        }
    }


    private boolean add(String key, int period, int count,int silencePeriod){
        long now = System.currentTimeMillis();
        key = KEY_PREFIX + key;
        String oldest = String.valueOf(now - period*1_000);
        String score = String.valueOf(now);
        String scoreValue = score;
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Long.class);
        //lua文件存放在resources目录下
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("alarm.lua")));
        List<String> keys = Arrays.asList(key, key+"_sp");
        Long x = stringRedisTemplate.execute(redisScript, keys, oldest,
                score, String.valueOf(count), scoreValue,  String.valueOf(silencePeriod));
        log.info("lua 返回结果：{}",x);
        return x == 1;
    }

}
