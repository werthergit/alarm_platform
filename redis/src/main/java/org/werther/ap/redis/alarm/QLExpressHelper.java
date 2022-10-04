package org.werther.ap.redis.alarm;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QLExpressHelper {

    public boolean execute(String express, Object value) throws Exception {
        ExpressRunner runner = new ExpressRunner();
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        context.put("threshold", value);
        Object r = runner.execute(express, context, null, true, false);
        log.info(" express result:{}",r);
        return (boolean) r;
    }

}
