package ap;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class AlarmCore {

    private AlarmRulesWatcher alarmRulesWatcher;

    AlarmCore(AlarmRulesWatcher alarmRulesWatcher) {
        this.alarmRulesWatcher = alarmRulesWatcher;
    }


    private LocalDateTime lastExecuteTime;

    public List<RunningRule> findRunningRule(String metricsName) {
        return alarmRulesWatcher.getRunningContext().get(metricsName);
    }

    public void start(List<AlarmCallback> allCallbacks) {
        LocalDateTime now = LocalDateTime.now();
        lastExecuteTime = now;

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                final List<AlarmMessage> alarmMessageList = new ArrayList<>(30);
                LocalDateTime checkTime = LocalDateTime.now();
                int minutes = Minutes.minutesBetween(lastExecuteTime, checkTime).getMinutes();
                boolean[] hasExecute = new boolean[]{false};

                log.info("checkTime:{}",checkTime);

                alarmRulesWatcher.getRunningContext().values().forEach(ruleList -> ruleList.forEach(runningRule -> {
                    log.info("minutes：{}", minutes);
                    if (minutes > 0) {

                        log.info("minutes>0");

                        runningRule.moveTo(checkTime);
                        /*
                         * Don't run in the first quarter per min, avoid to trigger false alarm.
                         */
                        log.info("checkTime.getSecondOfMinute is {}", checkTime.getSecondOfMinute());

                        if (checkTime.getSecondOfMinute() > 15) {
                            log.info("checkTime.getSecondOfMinute is true，{}", checkTime.getSecondOfMinute());

                            hasExecute[0] = true;
                            alarmMessageList.addAll(runningRule.check());
                        }
                    }
                }));
                // Set the last execute time, and make sure the second is `00`, such as: 18:30:00
                if (hasExecute[0]) {
                    lastExecuteTime = checkTime.minusSeconds(checkTime.getSecondOfMinute());
                }
                if (alarmMessageList.size() > 0) {
                    List<AlarmMessage> filteredMessages = alarmMessageList.stream().filter(msg -> !msg.isOnlyAsCondition()).collect(Collectors.toList());
                    if (filteredMessages.size() > 0) {
                        allCallbacks.forEach(callback -> callback.doAlarm(filteredMessages));
                        log.info("------doAlarm-----");
                    }
                }
            } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }, 10, 10, TimeUnit.SECONDS);
  }

}
