package ap;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class RunningRule {
    private static DateTimeFormatter TIME_BUCKET_FORMATTER = DateTimeFormat.forPattern("yyyyMMddHHmm");
    private final String ruleName;
    private final int period;
    private final String metricsName;

    private final int countThreshold;
    private final int silencePeriod;
    private final Map<MetaInAlarm, Window> windows;


    public RunningRule(AlarmRule alarmRule) {
        metricsName = alarmRule.getMetricsName();
        this.ruleName = alarmRule.getAlarmRuleName();
        windows = new ConcurrentHashMap<>();

        period = alarmRule.getPeriod();


        this.countThreshold = alarmRule.getCount();
        this.silencePeriod = alarmRule.getSilencePeriod();
    }

    public void in(MetaInAlarm meta, Metrics metrics) {
        Window window = windows.computeIfAbsent(meta, ignored -> new Window(period));
        window.add(metrics);
    }

    public void moveTo(LocalDateTime targetTime) {
        windows.values().forEach(window -> window.moveTo(targetTime));
    }

    public class Window {
        private LocalDateTime endTime;
        private int period;
        private int silenceCountdown;

        private LinkedList<Metrics> values;
        private ReentrantLock lock = new ReentrantLock();

        public Window(int period) {
            this.period = period;
            // -1 means silence countdown is not running.
            silenceCountdown = -1;
            init();
        }

        public void moveTo(LocalDateTime current) {
            lock.lock();
            try {
                if (endTime == null) {
                    init();
                } else {
                    int minutes = Minutes.minutesBetween(endTime, current).getMinutes();
                    if (minutes <= 0) {
                        return;
                    }
                    if (minutes > values.size()) {
                        // re-init
                        init();
                    } else {
                        for (int i = 0; i < minutes; i++) {
                            values.removeFirst();
                            values.addLast(null);
                        }
                    }
                }
                endTime = current;
            } finally {
                lock.unlock();
            }
            if (log.isTraceEnabled()) {
                log.trace("Move window {}", transformValues(values));
            }
        }

        public void add(Metrics metrics) {
            long bucket = metrics.getTimeBucket();

            log.info("add, bucket:{}",bucket);
            LocalDateTime timeBucket = TIME_BUCKET_FORMATTER.parseLocalDateTime(bucket + "");

            this.lock.lock();
            try {
                if (this.endTime == null) {
                    log.info("add, endTime == null, init, period:{}", period);
                    init();
                    this.endTime = timeBucket;
                }
                int minutes = Minutes.minutesBetween(timeBucket, this.endTime).getMinutes();
                if (minutes < 0) {
                    log.info("moveTo.....timeBucket:{}, endTime:{},, minutes:{} ", timeBucket, endTime, minutes);
                    this.moveTo(timeBucket);
                    minutes = 0;
                }

                if (minutes >= values.size()) {
                    // too old data
                    // also should happen, but maybe if agent/probe mechanism time is not right.
                    if (log.isTraceEnabled()) {
                        log.trace(
                                "Timebucket is {}, endTime is {} and value size is {}", timeBucket, this.endTime,
                                values.size()
                        );
                    }
                    return;
                }
                log.info("add, timeBucket:{}, endTime:{}, minutes:{}", timeBucket, endTime, minutes);
                this.values.set(values.size() - minutes - 1, metrics);

                log.info("add,values, key:{}, value:{}", values.size() - minutes - 1, metrics);

            } finally {
                this.lock.unlock();
            }
            if (log.isTraceEnabled()) {
                log.trace("Add metric {} to window {}", metrics, transformValues(this.values));
            }
        }

        public Optional<AlarmMessage> checkAlarm() {
            if (isMatch()) {
                /*
                 * When
                 * 1. Alarm trigger conditions are satisfied.
                 * 2. Isn't in silence stage, judged by SilenceCountdown(!=0).
                 */
                if (silenceCountdown < 1) {
                    silenceCountdown = silencePeriod;
                    return Optional.of(new AlarmMessage());
                } else {
                    silenceCountdown--;
                }
            } else {
                silenceCountdown--;
            }
            return Optional.empty();
        }

        private boolean isMatch() {
            // todo
//            if(1==1){
//                log.info(" isMatch.....");
//                return true;
//            }

            int matchCount = 0;
            for (Metrics metrics : values) {
                if (metrics == null) {
                    continue;
                }
                //todo
                matchCount++;
                log.info(" isMath().....matchCount:{}", matchCount);
                System.out.println("开始："+ OffsetDateTime.now());
            }

            if (log.isTraceEnabled()) {
                log.trace("Match count is {}, threshold is {}", matchCount, countThreshold);
            }
            log.info(" matchCount:{}, countThreshold:{}", matchCount, countThreshold);

            // Reach the threshold in current bucket.
            return matchCount >= countThreshold;
        }

        private void init() {
            values = new LinkedList<>();
            for (int i = 0; i < period; i++) {
                values.add(null);
            }
        }
    }

    private String transformValues(final LinkedList<Metrics> values) {
        values.forEach(m -> {
            log.info("TimeBucket:{} ", m.getTimeBucket());
        });
        return "";
    }

    public List<AlarmMessage> check() {
        List<AlarmMessage> alarmMessageList = new ArrayList<>(30);

        log.info("windows.size: {}",windows.size());

        windows.forEach((meta, window) -> {
            Optional<AlarmMessage> alarmMessageOptional = window.checkAlarm();
            if (alarmMessageOptional.isPresent()) {
                AlarmMessage alarmMessage = alarmMessageOptional.get();
                //alarmMessage.setScopeId(meta.getScopeId());
                //alarmMessage.setScope(meta.getScope());
                alarmMessage.setName(meta.getName());
                alarmMessage.setId0(meta.getId0());
                //alarmMessage.setId1(meta.getId1());
                alarmMessage.setRuleName(this.ruleName);
                //alarmMessage.setAlarmMessage(formatter.format(meta));
                //alarmMessage.setOnlyAsCondition(this.onlyAsCondition);
                alarmMessage.setStartTime(System.currentTimeMillis());
                alarmMessage.setPeriod(this.period);
                //alarmMessage.setTags(this.tags);
                alarmMessageList.add(alarmMessage);
            }
        });

        return alarmMessageList;
    }
}
