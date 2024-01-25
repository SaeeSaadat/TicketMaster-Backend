package tech.ayot.ticket.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import tech.ayot.ticket.backend.BackendApplication;

@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@WithUserDetails(BaseIntegrationTest.ADMIN_USER)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestPropertySource(locations = {"classpath:test.properties"})
@SpringBootTest(classes = {BackendApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class BaseIntegrationTest {

    protected static final String ADMIN_USER = "admin";

    public static PostgreSQLContainer<?> POSTGRES_CONTAINER = new PostgreSQLContainer<>(
        DockerImageName.parse("postgres:14.2-alpine")
    );

    static {
        POSTGRES_CONTAINER.start();
    }


    @Autowired
    private MockMvc mockMvc;


    @DynamicPropertySource
    public static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
    }


    public record MockMvcResponse<T>(T body, Cookie[] cookies) {
    }


    public <ReqT, ResT> MockMvcResponse<ResT> sendRequest(
        HttpMethod method,
        String url,
        MediaType mediaType,
        ReqT content,
        ResultMatcher expectation,
        Class<ResT> responseType
    ) throws Exception {
        RequestBuilder builder = buildRequest(
            method,
            url,
            mediaType,
            content
        );

        ResultActions resultActions = mockMvc.perform(builder);
        if (expectation != null) {
            resultActions = resultActions.andExpect(expectation);
        }
        MockHttpServletResponse response = resultActions.andReturn().getResponse();

        ObjectMapper objectMapper = BackendApplication.objectMapper;
        ResT body = objectMapper.readValue(response.getContentAsString(), responseType);

        return new MockMvcResponse<>(body, response.getCookies());
    }

    public <ReqT> MockMvcResponse<Object> sendRequest(
        HttpMethod method,
        String url,
        MediaType mediaType,
        ReqT content,
        ResultMatcher expectation
    ) throws Exception {
        RequestBuilder builder = buildRequest(
            method,
            url,
            mediaType,
            content
        );

        ResultActions resultActions = mockMvc.perform(builder);
        if (expectation != null) {
            resultActions.andExpect(expectation);
        }
        MockHttpServletResponse response = resultActions.andReturn().getResponse();

        return new MockMvcResponse<>(null, response.getCookies());
    }


    private <ReqT> RequestBuilder buildRequest(
        HttpMethod method,
        String url,
        MediaType mediaType,
        ReqT content
    ) throws Exception {
        ObjectMapper objectMapper = BackendApplication.objectMapper;
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.request(method, url);
        if (mediaType != null) {
            builder.contentType(mediaType);
        }
        if (content != null) {
            builder.content(objectMapper.writeValueAsString(content));
        }
        return builder;
    }
}
