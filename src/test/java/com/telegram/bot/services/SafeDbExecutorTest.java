package com.telegram.bot.services;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

class SafeDbExecutorTest {

    private static final Logger log = LoggerFactory.getLogger(SafeDbExecutorTest.class);
    private final SafeDbExecutor safeDbExecutor = new SafeDbExecutor();

    @Test
    void execute_shouldReturnValue_whenNoException() {
        String result = safeDbExecutor.execute(() -> "success", "Error occurred");
        assertThat(result).isEqualTo("success");
    }

    @Test
    void execute_shouldReturnNull_whenExceptionThrown() {
        String result = safeDbExecutor.execute(() -> {
            throw new RuntimeException("DB error");
        }, "DB failed");
        assertThat(result).isNull();
    }

    @Test
    void run_shouldExecuteWithoutException() {
        safeDbExecutor.run(() -> log.info("Running safely"), "Unexpected error");
        // просто проверяем, что исключение не выброшено
    }

    @Test
    void run_shouldHandleExceptionGracefully() {
        safeDbExecutor.run(() -> {
            throw new RuntimeException("Boom");
        }, "Handled error");
        // если дошли сюда — всё ок, исключение не вылетело наружу
    }
}

