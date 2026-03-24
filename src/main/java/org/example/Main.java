package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootApplication
// Remove @Controller from the class level, use it on methods or use @RestController
public class Main extends SpringBootServletInitializer {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Main.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    // REMOVED "/api" because it is already in your application.properties
    @GetMapping("/status") 
    @ResponseBody
    public String getStatus() {
        logger.info("Status check requested");
        return "Pipeline working beautifully, backend is running!!!!!!";
    }

    @GetMapping("/test-error")
    @ResponseBody
    public String triggerError() {
        logger.warn("Manual error triggered by developer!");
        throw new RuntimeException("This is a test exception for Dozzle colors!");
    }
}

// Separate the Advice to ensure it doesn't interfere with Actuator's internal routing
@RestControllerAdvice
class GlobalHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAll(Exception ex) {
        // If the error contains "static resource", it's a 404, not a 500
        if (ex.getMessage().contains("No static resource")) {
             return new ResponseEntity<>("Check your URL path! Error: " + ex.getMessage(), HttpStatus.NOT_FOUND);
        }
        logger.error("CRITICAL ERROR: {}", ex.getMessage(), ex); 
        return new ResponseEntity<>("Error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
