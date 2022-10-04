package org.werther.ap.redis.alarm;


public interface AlarmCallback {
    void doAlarm(AlarmMessage alarmMessage);
}
