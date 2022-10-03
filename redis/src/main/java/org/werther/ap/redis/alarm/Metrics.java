package org.werther.ap.redis.alarm;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Metrics {

    private String metricName;

    private String value;

    @Deprecated
    private long time;
}
