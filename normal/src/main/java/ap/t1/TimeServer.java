package ap.t1;

import java.util.concurrent.TimeUnit;

/**
 * @author
 * @data 2020-12-21 11:07
 * @description
 */
public class TimeServer {
    /**
     * 一毫秒有多少纳秒.
     */
    public static final long NANOS_PER_MILLI = 1000_000L;
    private static final long beginNanoTime;
    private static volatile long beginMilliTime;
    private static volatile long currentNanoTime;

    static {
        currentNanoTime = System.nanoTime();
        beginNanoTime = currentNanoTime;
        beginMilliTime = System.currentTimeMillis();
        Thread daemon = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    currentNanoTime = System.nanoTime();
                    try {
                        TimeUnit.NANOSECONDS.sleep(NANOS_PER_MILLI / 2);
                    } catch (Throwable ignore) {

                    }
                }
            }
        });
        daemon.setDaemon(true);
        daemon.setName("slidingwindow-time-tick-thread");
        daemon.start();
    }
}
