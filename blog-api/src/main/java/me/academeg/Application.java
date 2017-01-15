package me.academeg;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Arrays;

@SpringBootApplication
@ComponentScan("me.academeg")
@EnableJpaRepositories("me.academeg.dal.repository")
@Slf4j
public class Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        log.info("List of Beans:");
        Arrays
            .stream(context.getBeanDefinitionNames())
            .map(name -> "Bean loaded: " + name)
            .forEach(log::info);
        log.info("=================  READY  =================");
    }
}
