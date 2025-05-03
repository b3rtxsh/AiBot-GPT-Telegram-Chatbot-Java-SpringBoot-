package com.telegram.bot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class LoggingBeanPostProcessor implements BeanPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingBeanPostProcessor.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // You can log here before the bean's initialization logic
        LOGGER.debug("Creating bean '{}' of type {}", beanName, bean.getClass().getName());
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // If you prefer to log after initialization, do:
        // LOGGER.debug("Initialized bean '{}' of type {}", beanName, bean.getClass().getName());
        return bean;
    }
}