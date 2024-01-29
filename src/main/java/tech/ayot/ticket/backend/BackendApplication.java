package tech.ayot.ticket.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
    servers = @Server(url = "/", description = "Default Server URL")
)
@SpringBootApplication
public class BackendApplication {

    public static final ObjectMapper objectMapper = new ObjectMapper();


    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}
