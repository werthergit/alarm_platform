package ap.t1;

import java.util.Arrays;

public abstract class WindowBucket {

    protected int eventSize;

    /**
     * @param eventSize
     * @author
     * @date 2020-12-21 15:35
     */
    public WindowBucket(int eventSize) {
        this.eventSize = eventSize;
    }

    /**
     * @param eventType
     * @return long
     * @author
     * @date 2020-12-18 11:22
     */
    public void add(Enum eventType) {
        add(eventType, 1);
    }

    /**
     * @param eventType
     * @param value
     * @return long
     * @author
     * @date 2020-12-18 11:22
     */
    public abstract void add(Enum eventType, int value);

    /**
     * @param eventType
     * @return long
     * @author
     * @date 2020-12-24 17:13
     */
    public abstract long addAndGet(Enum eventType);

    /**
     * @param eventType
     * @return long
     * @author
     * @date 2020-12-21 15:36
     */
    public abstract long getValue(Enum eventType);

    /**
     * @author
     * @date 2020-12-21 17:28
     */
    public abstract void reset();

    /**
     * @return long[]
     * @author
     * @date 2020-12-21 17:29
     */
    public abstract long[] getValues();

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "eventValues=" + Arrays.toString(getValues()) +
                '}';
    }
}


