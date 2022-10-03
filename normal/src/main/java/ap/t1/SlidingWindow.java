package ap.t1;


import java.time.DateTimeException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

/**
 * @author
 * @data 2020-12-18 15:36
 * @description
 */
public abstract class SlidingWindow {

    protected ReentrantLock updateLock;
    protected Class<? extends Enum> eventTypeClass;
    protected SlidingWindowType windowType = SlidingWindowType.EFFICIENCY;
    protected EventTypeWrap<? extends Enum> eventTypeWrap;
    protected int bucketCount;
    protected int intervalInMs;
    protected int bucketLengthInMs;
    protected WindowBucketWrap[] windowBucketWraps;
    private long windowCreateTime;

    protected SlidingWindow() {

    }

    /**
     * @return com.xxxx.xxxx.common.slidingwindow.SlidingWindow
     * @author
     * @date 2020-12-18 16:58
     */
    protected SlidingWindow init() {
        if (eventTypeClass == null || bucketCount == 0 || intervalInMs == 0) {
            throw new IllegalArgumentException("init SlidingWindow error!");
        }
        updateLock = new ReentrantLock();
        eventTypeWrap = new EventTypeWrap(eventTypeClass);
        windowCreateTime = System.currentTimeMillis();
        windowBucketWraps = new WindowBucketWrap[bucketCount];
        IntStream.range(0, bucketCount).forEach(i -> {
            windowBucketWraps[i] = new WindowBucketWrap(0, buildWindowBucket());
        });
        bucketLengthInMs = intervalInMs / bucketCount;
        return this;
    }

    /**
     * @return com.xxxx.xxxx.common.slidingwindow.WindowBucket
     * @author
     * @date 2020-12-24 11:22
     */
    protected abstract WindowBucket buildWindowBucket();

    /**
     * @param eventType
     * @author
     * @date 2020-12-21 17:33
     */
    public void add(Enum eventType) {
        WindowBucket windowBucket = getCurrentBucket().getWindowBucket();
        windowBucket.add(eventType);
    }

    /**
     * @param eventType
     * @param value
     * @author
     * @date 2020-12-24 17:16
     */
    public void add(Enum eventType, int value) {
        WindowBucket windowBucket = getCurrentBucket().getWindowBucket();
        windowBucket.add(eventType, value);
    }

    /**
     * @param eventType
     * @return long
     * @author
     * @date 2020-12-24 17:13
     */
    public abstract long addAndSum(Enum eventType);

    /**
     * 类似LongAdder，提升性能，但是部分丢失精度
     *
     * @param eventType
     * @return long
     * @author
     * @date 2020-12-21 17:42
     */
    public abstract long sum(Enum eventType);

    /**
     * 申请，如果当前总和已经达到最大限制，则返回失败
     *
     * @param eventType
     * @param limit     最大限制
     * @return boolean
     * @author
     * @date 2021-01-11 12:51
     */
    public abstract boolean tryAcquire(Enum eventType, int limit);

    /**
     * @return com.xxxx.xxxx.common.slidingwindow.WindowBucketWrap
     * @author
     * @date 2020-12-18 16:56
     */
    public WindowBucketWrap getCurrentBucket() {
        long currentTime = getCalculateCurrentTime();
        int bucketId = calculateCurrentBucketId(currentTime);
        long windowStarTime = calculateWindowStartTime(currentTime);
        while (true) {
            WindowBucketWrap windowBucketWrap = windowBucketWraps[bucketId];
            long windowBucketBeginTime = windowBucketWrap.getBeginTime();
            if (windowBucketBeginTime == windowStarTime) {
                return windowBucketWrap;
            }
            // 新的一轮
            if (windowStarTime > windowBucketBeginTime) {
                resetWindow(windowBucketWrap, windowStarTime);
            } else {
                //由于用了nanoTime，不应该出现"时钟回拨"问题，所以直接抛异常
                throw new DateTimeException(
                        String.format("windowBucketBeginTime: %d, windowStarTime: %d, 当前时钟异常，请查看是否出现类似'时钟回拨'的问题！",
                                windowBucketBeginTime, windowStarTime));
            }
        }
    }

    /**
     * @param currentTime
     * @return int
     * @author
     * @date 2020-12-18 17:09
     */
    protected int calculateCurrentBucketId(long currentTime) {
        long timeId = currentTime / bucketLengthInMs;
        return (int) (timeId % windowBucketWraps.length);
    }

    /**
     * @param currentTime
     * @return long
     * @author
     * @date 2020-12-18 17:19
     */
    public long calculateWindowStartTime(long currentTime) {
        return currentTime - currentTime % bucketLengthInMs;
    }

    /**
     * 距离window创建时间过去多久
     *
     * @return long
     * @author
     * @date 2021-01-11 13:30
     */
    protected long getCalculateCurrentTime() {
        return System.currentTimeMillis() - windowCreateTime;
    }

    /**
     * @param windowBucketWrap
     * @param windowStarTime
     * @author
     * @date 2020-12-21 10:30
     */
    protected void resetWindow(WindowBucketWrap windowBucketWrap, long windowStarTime) {
        if (updateLock.tryLock()) {
            try {
                if (windowStarTime > windowBucketWrap.getBeginTime()) {
                    windowBucketWrap.reset(windowStarTime);
                }
            } finally {
                updateLock.unlock();
            }
        } else {
            // 如果继续循环，很可能其他线程还没操作完，先让出CPU给其他业务/线程
            Thread.yield();
        }
    }

    /**
     * @param currentTime
     * @param windowBucketWrap
     * @return boolean
     * @author
     * @date 2020-12-21 17:37
     */
    protected boolean isWindowDeprecated(long currentTime, WindowBucketWrap<WindowBucket> windowBucketWrap) {
        return currentTime - windowBucketWrap.getBeginTime() > intervalInMs;
    }

    public Class<? extends Enum> getEventTypeClass() {
        return eventTypeClass;
    }

    protected SlidingWindow setEventTypeClass(Class<? extends Enum> eventTypeClass) {
        this.eventTypeClass = eventTypeClass;
        return this;
    }

    /**
     * @return com.xxxx.xxxx.common.slidingwindow.EventTypeWrap<? extends java.lang.Enum>
     * @author
     * @date 2020-12-18 16:48
     */
    public EventTypeWrap<? extends Enum> getEventTypeWrap() {
        return eventTypeWrap;
    }

    /**
     * @return int
     * @author
     * @date 2020-12-18 16:56
     */
    public int getBucketCount() {
        return bucketCount;
    }

    /**
     * @param bucketCount
     * @return com.xxxx.xxxx.common.slidingwindow.SlidingWindow
     * @author
     * @date 2020-12-18 16:56
     */
    protected SlidingWindow setBucketCount(int bucketCount) {
        this.bucketCount = bucketCount;
        return this;
    }

    /**
     * @return int
     * @author
     * @date 2020-12-18 16:56
     */
    public int getIntervalInMs() {
        return intervalInMs;
    }

    /**
     * @param intervalInMs
     * @return com.xxxx.xxxx.common.slidingwindow.SlidingWindow
     * @author
     * @date 2020-12-18 16:56
     */
    protected SlidingWindow setIntervalInMs(int intervalInMs) {
        this.intervalInMs = intervalInMs;
        return this;
    }

    /**
     * @return com.xxxx.xxxx.common.slidingwindow.SlidingWindowType
     * @author
     * @date 2020-12-24 11:18
     */
    public SlidingWindowType getWindowType() {
        return windowType;
    }

    /**
     * @param windowType
     * @return com.xxxx.xxxx.common.slidingwindow.SlidingWindow
     * @author
     * @date 2020-12-24 11:18
     */
    protected SlidingWindow setWindowType(SlidingWindowType windowType) {
        this.windowType = windowType;
        return this;
    }

    /**
     * @return java.util.concurrent.locks.ReentrantLock
     * @author
     * @date 2021-01-11 13:04
     */
    public ReentrantLock getUpdateLock() {
        return updateLock;
    }

    @Override
    public String toString() {
        return "SlidingWindow{" +
                "eventTypeClass=" + eventTypeClass.getName() +
                ", windowType=" + windowType +
                ", bucketCount=" + bucketCount +
                ", intervalInMs=" + intervalInMs +
                ", bucketLengthInMs=" + bucketLengthInMs +
                ", windowCreateTime=" + windowCreateTime +
               // ", windowBucketWraps=\n" + StringUtils.join(windowBucketWraps, ",\n") +
                '}';
    }

    /**
     * @author
     * @date 2020-12-18 16:48
     */
    public static class SlidingWindowBuilder {

        private SlidingWindow slidingWindow;

        public SlidingWindowBuilder() {
            this(SlidingWindowType.EFFICIENCY);
        }

        public SlidingWindowBuilder(SlidingWindowType windowType) {
            if (SlidingWindowType.ACCURATE.equals(windowType)) {
                slidingWindow = new AccurateSlidingWindow();
            } else {
                slidingWindow = new EfficiencySlidingWindow();
            }
        }

        /**
         * @param eventTypeClass
         * @return com.xxxx.xxxx.common.slidingwindow.SlidingWindow.SlidingWindowBuilder
         * @author
         * @date 2020-12-18 16:48
         */
        public SlidingWindowBuilder ofEventTypeClass(Class<? extends Enum> eventTypeClass) {
            slidingWindow.setEventTypeClass(eventTypeClass);
            return this;
        }

        /**
         * @param bucketCount
         * @return com.xxxx.xxxx.common.slidingwindow.SlidingWindow.SlidingWindowBuilder
         * @author
         * @date 2020-12-18 16:55
         */
        public SlidingWindowBuilder ofBucketCount(int bucketCount) {
            slidingWindow.setBucketCount(bucketCount);
            return this;
        }

        /**
         * @param windowIntervalInMs
         * @return com.xxxx.xxxx.common.slidingwindow.SlidingWindow.SlidingWindowBuilder
         * @author
         * @date 2020-12-18 16:55
         */
        public SlidingWindowBuilder ofWindowIntervalInMs(int windowIntervalInMs) {
            slidingWindow.setIntervalInMs(windowIntervalInMs);
            return this;
        }

        /**
         * @return com.xxxx.xxxx.common.slidingwindow.SlidingWindow
         * @author
         * @date 2020-12-18 16:48
         */
        public SlidingWindow build() {
            return slidingWindow.init();
        }
    }
}
