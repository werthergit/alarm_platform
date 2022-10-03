package ap;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class NotifyHandler {
    private final AlarmCore core;


    public NotifyHandler(AlarmRulesWatcher alarmRulesWatcher) {
        core = new AlarmCore(alarmRulesWatcher);
    }

    public void notify(Metrics metrics) {

        //1、获得规则
        List<RunningRule> runningRules = core.findRunningRule(metrics.getMetricsName());
        //log.info("runningRules size：{}", runningRules.size());

        if (runningRules == null) {
            return;
        }

        MetaInAlarm metaInAlarm = new MetaInAlarm();
        metaInAlarm.setMetricsName(metrics.getMetricsName());
        metaInAlarm.setId0(metrics.getId());
        metaInAlarm.setName(metrics.getMetricsName());

       // metaInAlarm =
        //2、插入到滑动时间窗口中
        runningRules.forEach(rule -> rule.in(metaInAlarm, metrics));
        log.info("插入到滑动时间窗口中");
    }


    public void init(AlarmCallback... callbacks) {
        List<AlarmCallback> allCallbacks = new ArrayList<>(Arrays.asList(callbacks));
//        allCallbacks.add(new WebhookCallback(alarmRulesWatcher));
//        allCallbacks.add(new GRPCCallback(alarmRulesWatcher));
//        allCallbacks.add(new SlackhookCallback(alarmRulesWatcher));
//        allCallbacks.add(new WechatHookCallback(alarmRulesWatcher));
//        allCallbacks.add(new DingtalkHookCallback(alarmRulesWatcher));
//        allCallbacks.add(new FeishuHookCallback(alarmRulesWatcher));
//        allCallbacks.add(new EventHookCallback(this.manager));
//        allCallbacks.add(new WeLinkHookCallback(alarmRulesWatcher));
        core.start(allCallbacks);
    }
}
