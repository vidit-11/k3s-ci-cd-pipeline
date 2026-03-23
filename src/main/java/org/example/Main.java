package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootApplication
@Controller
public class Main extends SpringBootServletInitializer {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Main.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }


    @GetMapping("/api/status")
    @ResponseBody
    public String getStatus() {
        logger.info("Status check requested"); // Will show as GREEN/INFO
        return "Backend is running!";
    }

    @GetMapping("/api/test-error")
    @ResponseBody
    public String triggerError() {
        logger.warn("Manual error triggered by developer!"); // Will show as YELLOW
        throw new RuntimeException("This is a test exception for Dozzle colors!");
    }

    // --- GLOBAL EXCEPTION HANDLER ---
    // This ensures that when a JAR error happens, it is logged and sent to Dozzle
    @RestControllerAdvice
    class GlobalHandler {
        @ExceptionHandler(Exception.class)
        public ResponseEntity<String> handleAll(Exception ex) {
            logger.error("CRITICAL ERROR: {}", ex.getMessage(), ex); 
            return new ResponseEntity<>("Error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "{path:[^\\.]*}")
    public String redirect() {
        return "forward:/index.html";
    }
}
