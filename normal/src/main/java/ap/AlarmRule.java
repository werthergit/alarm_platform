package ap;

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
    private ArrayList<String> includeNames;
    private String includeNamesRegex;
    private ArrayList<String> excludeNames;
    private String excludeNamesRegex;
    private ArrayList<String> includeLabels;
    private String includeLabelsRegex;
    private ArrayList<String> excludeLabels;
    private String excludeLabelsRegex;
    //阈值
    private String threshold;
    //比较的方式
    private String op;
    //窗口大小
    private int period;
    private int count;
    private int silencePeriod;
    private String message;
    private boolean onlyAsCondition;
    private Map<String, String> tags;
}
