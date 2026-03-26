package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootApplication
@RestController 
@RequestMapping("/api")
public class Main extends SpringBootServletInitializer {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Main.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @GetMapping("/status") 
    public String getStatus() { 
        logger.info("Status check requested");
        return "Pipeline working beautifully, backend is running :D";
    }

    @GetMapping("/test-error")
    public String triggerError() {
        logger.warn("Manual error triggered by developer!");
        throw new RuntimeException("This is a test exception for Dozzle colors!");
    }
    @Bean
    public CommandLineRunner initDatabase(DataSource dataSource) {
        return args -> {
            try (Connection conn = dataSource.getConnection()) {
                logger.info("Hikari Pool initialized: " + conn.getMetaData().getDatabaseProductName());
            } catch (Exception e) {
                logger.error("Failed to initialize pool", e);
            }
        };
    }
}
