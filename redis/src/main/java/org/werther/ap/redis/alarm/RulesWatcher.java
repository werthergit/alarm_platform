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
    private volatile Map<String, AlarmRule> runningContext;

    public RulesWatcher(){
        runningContext = new HashMap<>();
        AlarmRule alarmRule = new AlarmRule();
        alarmRule.setThreshold("1000");
        //评估度量标准的时间长度，单位秒
        alarmRule.setPeriod(10);
        //度量有多少次符合告警条件后，才会触发告警
        alarmRule.setCount(4);
        alarmRule.setOp("<");

        runningContext.put("test01", alarmRule);
    }

    public AlarmRule findRunningRule(String metricsName) {
        return  getRunningContext().get(metricsName);
    }
}
