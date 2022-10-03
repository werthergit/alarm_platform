package ap.t1;

import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

/**
 * 强一致性
 *
 * @author
 * @data 2020-12-24 10:35
 * @description
 */
public class AccurateWindowBucket extends WindowBucket {

    private long[] eventValues;
    private ReentrantLock updateLock;

    /**
     * @param eventSize
     * @author
     * @date 2020-12-21 15:35
     */
    public AccurateWindowBucket(ReentrantLock updateLock, int eventSize) {
        super(eventSize);
        this.updateLock = updateLock;
        eventValues = new long[eventSize];
    }

    @Override
    public void add(Enum eventType, int value) {
        updateLock.lock();
        try {
            int index = eventType.ordinal();
            eventValues[index] = eventValues[index] + value;
        } finally {
            updateLock.unlock();
        }
    }

    @Override
    public long addAndGet(Enum eventType) {
        updateLock.lock();
        try {
            add(eventType);
            return getValue(eventType);
        } finally {
            updateLock.unlock();
        }
    }

    @Override
    public long getValue(Enum eventType) {
        updateLock.lock();
        try {
            return eventValues[eventType.ordinal()];
        } finally {
            updateLock.unlock();
        }
    }

    @Override
    public void reset() {
        updateLock.lock();
        try {
            IntStream.range(0, eventSize).forEach(i -> eventValues[i] = 0L);
        } finally {
            updateLock.unlock();
        }
    }

    @Override
    public long[] getValues() {
        updateLock.lock();
        try {
            return Arrays.copyOf(eventValues, eventValues.length);
        } finally {
            updateLock.unlock();
        }
    }
}
