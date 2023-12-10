package tech.ayot.ticket.backend.unit;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestPropertySource;

@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestPropertySource(locations = {"classpath:test.properties"})
public class BaseUnitTest {
}
