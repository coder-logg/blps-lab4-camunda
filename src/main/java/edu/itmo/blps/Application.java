package edu.itmo.blps;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableProcessApplication
public class Application {
  public static ApplicationContext CONTEXT;

  public static void main(String... args) {
    CONTEXT = SpringApplication.run(Application.class, args);
  }

  public static ApplicationContext getApplicationContext(){
    return CONTEXT;
  }

}