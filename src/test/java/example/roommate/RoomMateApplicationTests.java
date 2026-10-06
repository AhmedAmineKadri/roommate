package example.roommate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import example.roommate.Application.Service.RoomSynchronizerService;
import example.roommate.Application.Service.KeySynchronizerService;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:context_test;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.docker.compose.enabled=false",
        "spring.security.oauth2.client.registration.github.client-id=test-client",
        "spring.security.oauth2.client.registration.github.client-secret=test-secret"
})
class RoomMateApplicationTests {

    // Startup verification should not poll the external KeyMaster service.
    @MockBean RoomSynchronizerService roomSynchronizerService;
    @MockBean KeySynchronizerService keySynchronizerService;

    @Test
    void contextLoads() {
    }

}
