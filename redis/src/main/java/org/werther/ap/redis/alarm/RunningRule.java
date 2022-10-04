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
import java.util.Optional;

@Slf4j
@Component
public class RunningRule {

    @Autowired
    private AlarmCallback alarmCallback;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private String KEY_PREFIX = "lcp_alarm_";


    public void in(AlarmRule alarmRule, Metrics metrics){
        //加入window
        int period = alarmRule.getPeriod();
        //String threshold = alarmRule.getThreshold();
        int count = alarmRule.getCount();
        //静默时长
        int silencePeriod = alarmRule.getSilencePeriod();

        //表达式为true OR 空，触发告警判断
        String expression = alarmRule.getThresholdExpressions();
        if("".equals(expression) || expression==null || isMatch(expression, metrics) ){
            boolean isAlarm = checkAlarm(metrics.getMetricName(), period, count, silencePeriod);
            if( isAlarm ){
                alarmCallback.doAlarm();
            }
        }
    }

    private boolean isMatch(String expression, Metrics metrics) {
        QLExpressHelper qlExpressHelper = new QLExpressHelper();
        try {
           return qlExpressHelper.execute(expression, metrics.getValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean checkAlarm(String key, int period, int count, int silencePeriod){
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
