package com.example.library.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractJob implements Job {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public final void execute() {
        log.info("Job [{}] started", name());
        long start = System.currentTimeMillis();
        try {
            doExecute();
            log.info("Job [{}] finished in {} ms", name(), System.currentTimeMillis() - start);
        } catch (RuntimeException e) {
            log.error("Job [{}] failed after {} ms: {}", name(), System.currentTimeMillis() - start, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Job [{}] failed after {} ms: {}", name(), System.currentTimeMillis() - start, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    protected abstract void doExecute();
}
