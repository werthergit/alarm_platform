package ap;

import lombok.Getter;

@Getter
public class AlarmProvider {

    private AlarmRulesWatcher alarmRulesWatcher;
    private NotifyHandler notifyHandler;

    public String name() {
        return "default";
    }

    public void prepare() throws  Exception {

        //1、读取规则文件

        //2、监听规则变动
        alarmRulesWatcher = new AlarmRulesWatcher();

        //3、监听数据变动
        notifyHandler = new NotifyHandler(alarmRulesWatcher);
        notifyHandler.init();
       // this.registerServiceImplementation(MetricsNotify.class, notifyHandler);
    }





}
