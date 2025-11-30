package com.telegram.bot.services;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;

@Component
public class DefaultTelegramExecutor implements TelegramExecutor {

    private final DefaultAbsSender absSender;

    public DefaultTelegramExecutor(@Lazy DefaultAbsSender absSender) {
        this.absSender = absSender;
    }

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) throws TelegramApiException {
        return absSender.execute(method);
    }
}

