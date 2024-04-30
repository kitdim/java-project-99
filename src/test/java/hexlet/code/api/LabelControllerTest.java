package hexlet.code.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
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
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
        labelRepository.save(testLabel);
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
        MvcResult result = mockMvc.perform(get("/api/labels/{id}", testLabel.getId()).with(token))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        assertThatJson(body)
                .and(v -> v.node("name").isEqualTo(testLabel.getName()));
    }
    @Test
    @DisplayName("Test create the label")
    public void testCreate() throws Exception {
        LabelCreateDTO dto = new LabelCreateDTO();
        dto.setName("something");

        MockHttpServletRequestBuilder request = post("/api/labels")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        Label someLabel = labelRepository.findByName(dto.getName()).get();
        assertThat(someLabel).isNotNull();
        assertThat(someLabel.getName()).isEqualTo(dto.getName());
    }
    @Test
    @DisplayName("Test update the label")
    public void testUpdate() throws Exception {
        LabelUpdateDTO dto = new LabelUpdateDTO();
        dto.setName(JsonNullable.of("something"));

        MockHttpServletRequestBuilder request = put("/api/labels/{id}", testLabel.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        Label someLabel = labelRepository.findByName("something").get();
        assertThat(someLabel.getName()).isEqualTo("something");
        assertThat(someLabel.getTasks().size()).isEqualTo(testLabel.getTasks().size());
    }
}
