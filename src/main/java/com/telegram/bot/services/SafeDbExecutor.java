package com.telegram.bot.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class SafeDbExecutor {

    private static final Logger log = LoggerFactory.getLogger(SafeDbExecutor.class);

    public <T> T execute(Supplier<T> action, String errorMsg) {
        try {
            return action.get();
        } catch (Exception e) {
            log.error(errorMsg, e);
            return null;
        }
    }

    public void run(Runnable action, String errorMsg) {
        try {
            action.run();
        } catch (Exception e) {
            log.error(errorMsg, e);
        }
    }
}
