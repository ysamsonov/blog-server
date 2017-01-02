package me.academeg.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("me.academeg")
@EnableJpaRepositories("me.academeg.api.repository")
@Slf4j
public class Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        log.info("List of Beans:");
        for (String beanName : context.getBeanDefinitionNames()) {
            log.info("Bean loaded: " + beanName);
        }
        log.info("=================  READY  =================");
    }
}
