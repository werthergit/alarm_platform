package ap.t1;

/**
 * @author
 * @data 2020-12-18 11:17
 * @description
 */
public class WindowBucketWrap<T extends WindowBucket> {

    private volatile long beginTime;
    private T windowBucket;

    public WindowBucketWrap(long beginTime, T windowBucket) {
        this.beginTime = beginTime;
        this.windowBucket = windowBucket;
    }

    /**
     * @param windowStarTime
     * @author
     * @date 2020-12-21 10:14
     */
    public void reset(long windowStarTime) {
        windowBucket.reset();
        beginTime = windowStarTime;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public WindowBucketWrap<T> setBeginTime(long beginTime) {
        this.beginTime = beginTime;
        return this;
    }

    public T getWindowBucket() {
        return windowBucket;
    }

    @Override
    public String toString() {
        return "WindowBucketWrap{" +
                "beginTime=" + beginTime +
                ", windowBucket=" + windowBucket +
                '}';
    }
}
