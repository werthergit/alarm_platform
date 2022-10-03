package ap;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 从配置中心获得规则的变化后的信息
 */
public class AlarmRulesWatcher {
    @Getter
    private volatile Map<String, List<RunningRule>> runningContext;

    public AlarmRulesWatcher(){
        runningContext = new HashMap<>();
        AlarmRule alarmRule = new AlarmRule();
        alarmRule.setAlarmRuleName(Normal.metricsName);
        alarmRule.setThreshold("1000");
        //评估度量标准的时间长度
        alarmRule.setPeriod(3);
        //度量有多少次符合告警条件后，才会触发告警
        alarmRule.setCount(2);
        alarmRule.setOp("<");
        RunningRule runningRule = new RunningRule(alarmRule);
        List<RunningRule> runningRules = new ArrayList<>();
        runningRules.add(runningRule);

        runningContext.put(Normal.metricsName, runningRules);
    }



}
