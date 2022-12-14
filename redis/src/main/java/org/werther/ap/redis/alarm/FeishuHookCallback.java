package org.werther.ap.redis.alarm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FeishuHookCallback implements AlarmCallback{
    @Override
    public void doAlarm(AlarmMessage alarmMessage) {
        log.info("飞书提醒,rule:{}，MetricName:{}",alarmMessage.getRuleName(), alarmMessage.getMetricName());
    }
}
