package ap.t1;

/**
 * @author
 * @data 2020-12-24 17:26
 * @description
 */
public class AccurateSlidingWindow extends SlidingWindow {

    @Override
    public WindowBucket buildWindowBucket() {
        int eventTypeSize = eventTypeWrap.getEventTypes().length;
        return new AccurateWindowBucket(updateLock, eventTypeSize);
    }

    @Override
    public long addAndSum(Enum eventType) {
        updateLock.lock();
        try {
            WindowBucket windowBucket = getCurrentBucket().getWindowBucket();
            windowBucket.add(eventType);
            return sum(eventType);
        } finally {
            updateLock.unlock();
        }
    }

    @Override
    public long sum(Enum eventType) {
        updateLock.lock();
        try {
            long value = 0;
            long currentTime = getCalculateCurrentTime();
            for (WindowBucketWrap windowBucketWrap : windowBucketWraps) {
                if (!isWindowDeprecated(currentTime, windowBucketWrap)) {
                    value += windowBucketWrap.getWindowBucket().getValue(eventType);
                }
            }
            return value;
        } finally {
            updateLock.unlock();
        }
    }

    @Override
    public boolean tryAcquire(Enum eventType, int limit) {
        updateLock.lock();
        try {
            if (sum(eventType) == limit) {
                return false;
            }
            add(eventType);
            return true;
        } finally {
            updateLock.unlock();
        }
    }
}
