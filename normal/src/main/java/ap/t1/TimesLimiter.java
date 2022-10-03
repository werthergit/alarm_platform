//package org.werther.ap.t1;
//
///**
// *
// * @author
// * @data 2020-06-02 10:51
// * @description
// */
//public class TimesLimiter implements Limiter {
//
//    private static final int SLIDINGWINDOW_BUCKETCOUNT = 10;
//    private int timesLimit;
//    private SlidingWindow slidingWindow;
//
//    public TimesLimiter(int timesLimitSeconds, int timesLimit) {
//        this.timesLimit = timesLimit;
//        slidingWindow = new SlidingWindow.SlidingWindowBuilder(SlidingWindowType.ACCURATE)
//                .ofEventTypeClass(StandardLevel.class)
//                .ofBucketCount(SLIDINGWINDOW_BUCKETCOUNT)
//                .ofWindowIntervalInMs(timesLimitSeconds * 1000)
//                .build();
//    }
//
//    @Override
//    public boolean isLimited(String content) {
//        return !slidingWindow.tryAcquire(StandardLevel.ERROR, timesLimit);
//    }
//}
