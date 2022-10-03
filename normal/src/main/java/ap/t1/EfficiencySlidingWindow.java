package ap.t1;

/**
 * @author
 * @data 2020-12-24 17:26
 * @description
 */
public class EfficiencySlidingWindow extends SlidingWindow {

    @Override
    public WindowBucket buildWindowBucket() {
        int eventTypeSize = eventTypeWrap.getEventTypes().length;
        return new EfficiencyWindowBucket(eventTypeSize);
    }

    @Override
    public long addAndSum(Enum eventType) {
        add(eventType);
        return sum(eventType);
    }

    @Override
    public long sum(Enum eventType) {
        long value = 0;
        long currentTime = getCalculateCurrentTime();
        for (WindowBucketWrap windowBucketWrap : windowBucketWraps) {
            if (!isWindowDeprecated(currentTime, windowBucketWrap)) {
                value += windowBucketWrap.getWindowBucket().getValue(eventType);
            }
        }
        return value;
    }

    @Override
    public boolean tryAcquire(Enum eventType, int limit) {
        if (sum(eventType) >= limit) {
            return false;
        }
        add(eventType);
        return true;
    }
}
