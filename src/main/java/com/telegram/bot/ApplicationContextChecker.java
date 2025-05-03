package com.telegram.bot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class ApplicationContextChecker implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationContextChecker.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext ctx = event.getApplicationContext();
        logger.info("ApplicationContext initialized with {} beans.", ctx.getBeanDefinitionCount());

        // Optionally, log all bean names:
        Arrays.stream(ctx.getBeanDefinitionNames())
                .sorted()
                .forEach(beanName -> logger.debug("Bean: {}", beanName));
    }
}
