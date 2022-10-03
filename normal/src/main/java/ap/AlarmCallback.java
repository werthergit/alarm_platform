package ap;

import java.util.List;

public interface AlarmCallback {
    void doAlarm(List<AlarmMessage> alarmMessage);
}
