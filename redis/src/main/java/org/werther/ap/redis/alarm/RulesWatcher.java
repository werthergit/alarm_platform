package org.werther.ap.redis.alarm;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RulesWatcher {
    @Getter
    private volatile Map<String, List<AlarmRule>> runningContext;

    public RulesWatcher(){
        runningContext = new HashMap<>();
        AlarmRule alarmRule = new AlarmRule();
        alarmRule.setAlarmRuleName("r01");
        //评估度量标准的时间长度，单位秒
        alarmRule.setPeriod(10);
        //度量有多少次符合告警条件后，才会触发告警
        alarmRule.setCount(4);
        alarmRule.setThresholdExpressions("threshold > 1000");
        alarmRule.setSilencePeriod(5);
        alarmRule.setMetricsName("m01");


        AlarmRule alarmRule2 = new AlarmRule();
        alarmRule2.setAlarmRuleName("r02");
        //评估度量标准的时间长度，单位秒
        alarmRule2.setPeriod(10);
        //度量有多少次符合告警条件后，才会触发告警
        alarmRule2.setCount(3);
        alarmRule2.setThresholdExpressions("threshold > 999");
        alarmRule2.setSilencePeriod(5);
        alarmRule2.setMetricsName("m01");

        AlarmRule alarmRule3 = new AlarmRule();
        alarmRule3.setAlarmRuleName("r03");
        //评估度量标准的时间长度，单位秒
        alarmRule3.setPeriod(10);
        //度量有多少次符合告警条件后，才会触发告警
        alarmRule3.setCount(3);
        //alarmRule3.setThresholdExpressions("threshold < 999");
        alarmRule3.setSilencePeriod(3);
        alarmRule3.setMetricsName("m01");

        List<AlarmRule> alarmRuleList = new ArrayList<>();
        alarmRuleList.add(alarmRule);
        alarmRuleList.add(alarmRule2);
        alarmRuleList.add(alarmRule3);

        runningContext.put(alarmRule.getMetricsName(), alarmRuleList);
    }

    public List<AlarmRule> findRunningRule(String metricsName) {
        return  getRunningContext().get(metricsName);
    }
}
