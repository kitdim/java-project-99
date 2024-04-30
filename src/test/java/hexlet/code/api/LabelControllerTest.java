package hexlet.code.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.Label;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.TestUtils;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LabelControllerTest {
    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TestUtils testUtils;

    private JwtRequestPostProcessor token;
    private User testUser;
    private Label testLabel;

    @BeforeEach
    public void setUp() {
        testUser = Instancio.of(testUtils.getUserModel()).create();
        userRepository.save(testUser);
        token = jwt().jwt(builder -> builder.subject(testUser.getUsername()));

        testLabel = Instancio.of(testUtils.getLabelModel()).create();
    }

    @AfterEach
    public void clean() {
        testUtils.clean();
    }

    @Test
    @DisplayName("Test find all")
    public void testIndex() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/labels").with(token))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    @DisplayName("Test find by id")
    public void testFindById() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/tasks/{id}", testLabel.getId()).with(token))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        assertThatJson(body)
                .and(v -> v.node("name").isEqualTo(testLabel.getName()));
    }
}
