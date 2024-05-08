package hexlet.code.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.model.User;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
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

    @BeforeEach
    public void setUp() {
        testUser = Instancio.of(testUtils.getUserModel()).create();
        token = jwt().jwt(builder -> builder.subject(testUser.getUsername()));
        userRepository.save(testUser);
    }

    @AfterEach
    public void clean() {
        testUtils.clean();
    }

    @Test
    @DisplayName("Test find all")
    public void testIndex() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/users").with(token))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        List<User> users = objectMapper.readValue(body, new TypeReference<List<User>>() {
        });
        List<User> expected = userRepository.findAll();

        assertThat(users).containsAll(expected);
    }

    @Test
    @DisplayName("Test find user by id")
    public void testShow() throws Exception {
        Long id = testUser.getId();
        MvcResult result = mockMvc.perform(get("/api/users/{id}", id).with(token))
                .andDo(print())
                .andReturn();
        UserDTO dto = objectMapper.readValue(result.getResponse().getContentAsString(), UserDTO.class);
        assertEquals(testUser.getFirstName(), dto.getFirstName());
        assertEquals(testUser.getLastName(), dto.getLastName());
    }

    @Test
    @DisplayName("Test find user with a bad id")
    public void testShowWithBadId() throws Exception {
        Long id = 55L;
        MvcResult result = mockMvc.perform(get("/api/users/{id}", id).with(token))
                .andDo(print())
                .andReturn();
        assertEquals(404, result.getResponse().getStatus());
    }

    @Test
    @DisplayName("Test create user")
    public void testCreate() throws Exception {
        User someUser = Instancio.of(testUtils.getUserModel()).create();
        MockHttpServletRequestBuilder request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(someUser)).with(token);
        mockMvc.perform(request)
                .andExpect(status().isCreated());
        User user = userRepository.findByEmail(someUser.getEmail()).get();

        assertNotNull(user);
        assertThat(user.getFirstName()).isEqualTo(someUser.getFirstName());
        assertThat(user.getLastName()).isEqualTo(someUser.getLastName());
    }

    @Test
    @DisplayName("Test create with a bad value")
    public void testCreateWithBadError() throws Exception {
        User someUser = Instancio.of(testUtils.getUserModel()).create();
        someUser.setEmail("bad-email");
        someUser.setPasswordDigest("21");

        MockHttpServletRequestBuilder request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(someUser)).with(token);
        mockMvc.perform(request)
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Test update user")
    public void testUpdate() throws Exception {
        Long id = testUser.getId();
        HashMap<String, String> data = new HashMap<>();
        data.put("firstName", "Ivan");

        MockHttpServletRequestBuilder request = put("/api/users/{id}", id)
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));

        mockMvc.perform(request).andExpect(status().isOk());
        User someUser = userRepository.findById(testUser.getId()).get();
        assertThat(someUser.getFirstName()).isEqualTo(("Ivan"));
    }

    @Test
    @DisplayName("Test update user with a bad value")
    public void testUpdateWithBadValue() throws Exception {
        Long id = testUser.getId();
        HashMap<String, String> data = new HashMap<>();
        data.put("firstName", "Ivan");
        data.put("password", "11");
        data.put("email", "bad-email");

        MockHttpServletRequestBuilder request = put("/api/users/{id}", id)
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Delete by id")
    public void deleteTest() throws Exception {
        Long id = testUser.getId();
        MockHttpServletRequestBuilder request = delete("/api/users/{id}", id).with(token);
        mockMvc.perform(request).andExpect(status().isNoContent());
        assertFalse(userRepository.findById(id).isPresent());
    }

    @Test
    @DisplayName("Test find all without auth")
    public void testIndexWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }
}
