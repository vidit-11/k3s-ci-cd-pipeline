package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController; // <-- Import this
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootApplication
@RestController // <-- CRITICAL: You must add this back so Spring registers the @GetMapping methods below
public class Main extends SpringBootServletInitializer {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Main.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    // Since your application.properties has server.servlet.context-path=/api
    // This will correctly resolve to /api/status
    @GetMapping("/status") 
    public String getStatus() { // @ResponseBody is implicit with @RestController
        logger.info("Status check requested");
        return "Pipeline working beautifully, backend is running!!!!!!";
    }

    @GetMapping("/test-error")
    public String triggerError() {
        logger.warn("Manual error triggered by developer!");
        throw new RuntimeException("This is a test exception for Dozzle colors!");
    }
}
