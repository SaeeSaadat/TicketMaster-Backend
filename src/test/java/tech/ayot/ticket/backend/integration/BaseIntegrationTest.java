package tech.ayot.ticket.backend.integration;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import tech.ayot.ticket.backend.BackendApplication;

@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@WithUserDetails(BaseIntegrationTest.ADMIN_USER)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestPropertySource(locations = {"classpath:test.properties"})
@SpringBootTest(classes = {BackendApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class BaseIntegrationTest {

    protected static final String ADMIN_USER = "admin";

    public static PostgreSQLContainer<?> POSTGRES_CONTAINER = new PostgreSQLContainer<>(
        DockerImageName.parse("postgres:latest")
    );

    static {
        POSTGRES_CONTAINER.start();
    }


    @DynamicPropertySource
    public static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
    }
}
