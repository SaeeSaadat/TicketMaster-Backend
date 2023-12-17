package tech.ayot.ticket.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

    public static final ObjectMapper objectMapper = new ObjectMapper();


    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}
