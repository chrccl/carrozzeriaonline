package it.chrccl.carrozzeriaonline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CarrozzeriaOnlineApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(CarrozzeriaOnlineApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(CarrozzeriaOnlineApplication.class);
    }

}
