package spencercjh.top.fastrun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author spencercjh
 */
@EnableScheduling
@SpringBootApplication(scanBasePackages = {"spencercjh.top.fastrun"})
public class FastRunApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(FastRunApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(FastRunApplication.class, args);
    }

}
