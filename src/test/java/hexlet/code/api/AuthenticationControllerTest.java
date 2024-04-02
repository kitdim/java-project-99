package hexlet.code.api;

import hexlet.code.dto.AuthRequest;
import hexlet.code.util.UserUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest extends BaseTest {
    @Test
    public void testCreateAdmin() throws Exception {
        var authRequest = new AuthRequest();
        authRequest.setUsername(UserUtils.ADMIN_EMAIL);
        authRequest.setPassword(UserUtils.ADMIN_PASSWORD);

        var request = post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest));

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }
}
