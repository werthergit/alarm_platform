package ap;

import lombok.Getter;
import lombok.Setter;

public class Metrics {

    public static final String TIME_BUCKET = "time_bucket";
    public static final String ENTITY_ID = "entity_id";

    /**
     * Time attribute
     */
    @Getter
    @Setter
    private long timeBucket;

    @Setter
    @Getter
    private String metricsName;

    @Setter
    @Getter
    private String id;


}
