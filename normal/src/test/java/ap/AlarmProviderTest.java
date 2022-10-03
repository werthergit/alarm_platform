package ap;

import org.junit.Before;
import org.junit.Test;

import java.time.OffsetDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.mock;

public class AlarmProviderTest {
    private AlarmProvider alarmProvider;

    @Before
    public void setUp() throws Exception {
        //ServiceLoader<AlarmProvider> serviceLoader = ServiceLoader.load(AlarmProvider.class);
        //Iterator<AlarmProvider> providerIterator = serviceLoader.iterator();

       // assertTrue(providerIterator.hasNext());

        alarmProvider = new AlarmProvider();

        alarmProvider.prepare();


    }

    @Test
    public void name() {
        System.out.println("开始："+ OffsetDateTime.now());
        assertEquals("default", alarmProvider.name());

        for(int i=0;i<1000;i++){
            Metrics metrics = new Metrics();
            metrics.setMetricsName(Normal.metricsName);
            OffsetDateTime asdt  = OffsetDateTime.now();
            //202209291703
            //System.out.println(asdt.getYear()*100000000L);
            //System.out.println(asdt.getYear()*100000000L+asdt.getMonth().getValue()*1000000L+asdt.getDayOfMonth()*10000L+asdt.getHour()*100+asdt.getMinute());
            long bucket1 = asdt.getYear()*100000000L+asdt.getMonth().getValue()*1000000L+asdt.getDayOfMonth()*10000L+asdt.getHour()*100+asdt.getMinute();
            metrics.setTimeBucket(bucket1);
            metrics.setId("1001");
            alarmProvider.getNotifyHandler().notify(metrics);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


//        try {
//            Thread.sleep(30000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }





    }



}
