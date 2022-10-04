package org.werther.ap.redis.alarm;

import lombok.*;

import java.util.ArrayList;
import java.util.Map;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class AlarmRule {
    private String alarmRuleName;

    private String metricsName;
    //窗口大小，单位秒
    private int period;
    //计数（count）。 在一个周期窗口中，如果值秒超过阈值（按周期统计），达到计数值，需要发送警报。
    private int count;
    //告警抑制，再触发告警之后的下面一段时间周期，相同的告警不再触发【防止骚扰】
    private int silencePeriod;
    //阈值判断表达式，当该表达式结果为true或者是空的时候，才会触发警报，表达式参数为threshold，例如：threshold>1000
    private String thresholdExpressions;

}
