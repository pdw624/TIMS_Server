package kr.tracom.platform.common.log;

import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluator;
import ch.qos.logback.core.spi.ContextAwareBase;
import lombok.Setter;

public class TimeBasedEventEvaluator extends ContextAwareBase implements EventEvaluator {
    private long beforeTime = 0;
    @Setter
    private long intervalTime = 1000 * 60;

    @Override
    public boolean evaluate(Object event) throws NullPointerException, EvaluationException {
        long current = System.currentTimeMillis();
        long backupBeforeTime = this.beforeTime;

        if (current - backupBeforeTime > intervalTime) {
            this.beforeTime = current;
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isStarted() {
        return false;
    }
}
