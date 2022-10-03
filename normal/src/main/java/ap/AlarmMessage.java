package ap;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class AlarmMessage {
    private int scopeId;
    private String scope;
    private String name;
    private String id0;
    private String id1;
    private String ruleName;
    private String alarmMessage;
    //private List<Tag> tags;
    private long startTime;
    private transient int period;
    private transient boolean onlyAsCondition;
}
