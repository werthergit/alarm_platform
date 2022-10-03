package ap.t1;

import java.util.concurrent.atomic.LongAdder;
import java.util.stream.IntStream;

/**
 * 性能优先，最终一致性
 *
 * @author
 * @date 2020-12-24 11:02
 */
public class EfficiencyWindowBucket extends WindowBucket {

    private LongAdder[] eventValues;

    /**
     * @param eventSize
     * @author
     * @date 2020-12-21 15:35
     */
    public EfficiencyWindowBucket(int eventSize) {
        super(eventSize);
        eventValues = new LongAdder[eventSize];
        IntStream.range(0, eventSize).forEach(i -> {
            eventValues[i] = new LongAdder();
        });
    }

    @Override
    public void add(Enum eventType, int value) {
        eventValues[eventType.ordinal()].add(value);
    }

    @Override
    public long addAndGet(Enum eventType) {
        add(eventType);
        return getValue(eventType);
    }

    @Override
    public long getValue(Enum eventType) {
        return eventValues[eventType.ordinal()].longValue();
    }

    @Override
    public void reset() {
        for (LongAdder value : eventValues) {
            value.reset();
        }
    }

    @Override
    public long[] getValues() {
        long[] values = new long[eventSize];
        IntStream.range(0, eventSize).forEach(i -> {
            values[i] = eventValues[i].longValue();
        });
        return values;
    }
}
