package org.werther.ap;


import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class Window {
    private static DateTimeFormatter TIME_BUCKET_FORMATTER = DateTimeFormat.forPattern("yyyyMMddHHmm");

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

        LocalDateTime timeBucket = TIME_BUCKET_FORMATTER.parseLocalDateTime(bucket + "");

        this.lock.lock();
        try {
            if (this.endTime == null) {
                init();
                this.endTime = timeBucket;
            }
            int minutes = Minutes.minutesBetween(timeBucket, this.endTime).getMinutes();
            if (minutes < 0) {
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

            this.values.set(values.size() - minutes - 1, metrics);
        } finally {
            this.lock.unlock();
        }
        if (log.isTraceEnabled()) {
            log.trace("Add metric {} to window {}", metrics, transformValues(this.values));
        }
    }

    private void init() {
        values = new LinkedList<>();
        for (int i = 0; i < period; i++) {
            values.add(null);
        }
    }

    private String transformValues(final LinkedList<Metrics> values) {
        values.forEach(m -> {
            log.info("TimeBucket:{} ", m.getTimeBucket());
        });
        return "";
    }

}
