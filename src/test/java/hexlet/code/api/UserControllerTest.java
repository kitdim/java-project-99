package hexlet.code.api;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.UserUtils;
import hexlet.code.utils.TestUtils;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.HashMap;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@SpringBootTest
@AutoConfigureMockMvc
public final class UserControllerTest extends BaseTest {
    @Autowired
    private UserRepository repository;
    @Autowired
    private TestUtils testUtils;
    private JwtRequestPostProcessor token;

    @BeforeEach
    public void setUp() {
        token = jwt().jwt(builder -> builder.subject(UserUtils.ADMIN_EMAIL));
    }

    @AfterEach
    public void clean() {
        testUtils.clean();
    }

    @Test
    public void testIndex() throws Exception {
        mockMvc.perform(get("/api/users").with(token))
                .andExpect(status().isOk());
        Optional<User> isHasAdmin = repository.findByEmail("hexlet@example.com").stream().findFirst();
        assertTrue(isHasAdmin.isPresent());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void testCreate(boolean isSuccess) throws Exception {
        long id = 15L;
        User user = testUtils.generateUser();
        if (isSuccess) {
            repository.save(user);
            id = user.getId();
        }
        MvcResult result = mockMvc.perform(get("/api/users/{id}", id).with(jwt()))
                .andDo(print())
                .andReturn();
        if (isSuccess) {
            UserDto dto = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
            assertEquals(user.getFirstName(), dto.getFirstName());
            assertEquals(user.getLastName(), dto.getLastName());
        } else {
            assertEquals(404, result.getResponse().getStatus());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"valid", "invalidValue"})
    public void testCreate(String result) throws Exception {
        User data = testUtils.generateUser();
        if (result.equals("invalidValue")) {
            data.setEmail("test");
            data.setPassword("1");
        }
        MockHttpServletRequestBuilder request = post("/api/users").with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));
        if (result.equals("valid")) {
            mockMvc.perform(request)
                    .andExpect(status().isCreated());

            User user = repository.findByEmail(data.getEmail()).get();

            assertNotNull(user);
            assertThat(user.getFirstName()).isEqualTo(data.getFirstName());
            assertThat(user.getLastName()).isEqualTo(data.getLastName());
        } else {
            mockMvc.perform(request)
                    .andExpect(status().isBadRequest());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"ok", "notFound", "invalidValue"})
    public void testUpdate(String result) throws Exception {
        HashMap<String, String> data = new HashMap<>();
        User user = testUtils.generateUser();
        repository.save(user);
        data.put("firstName", "Ivan");
        long id = user.getId();
        switch (result) {
            case "notFound":
                id = 15L;
                break;
            case "invalidValue":
                data.put("password", "11");
                data.put("email", "test");
                break;
            default:
                break;
        }

        MockHttpServletRequestBuilder request = put("/api/users/{id}", id)
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));

        switch (result) {
            case "ok":
                mockMvc.perform(request)
                        .andExpect(status().isOk());
                User test = repository.findById(user.getId()).get();
                assertThat(test.getFirstName()).isEqualTo(("Ivan"));
                break;
            case "notFound":
                mockMvc.perform(request)
                        .andExpect(status().isNotFound());
                break;
            case "invalidValue":
                mockMvc.perform(request)
                        .andExpect(status().isBadRequest());
                break;
            default:
                break;
        }
    }

    @Test
    public void testDelete() throws Exception {
        User user = testUtils.generateUser();
        repository.save(user);
        long id = user.getId();
        MockHttpServletRequestBuilder request = delete("/api/users/{id}", id).with(jwt());

        mockMvc.perform(request).andExpect(status().isNoContent());
        assertFalse(repository.findById(id).isPresent());
    }

    @Test
    public void testIndexWithoutAuth() throws Exception {
        User user = testUtils.generateUser();
        repository.save(user);
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testShowWithoutAuth() throws Exception {
        User user = testUtils.generateUser();
        repository.save(user);
        var request = get("/api/users/{id}", user.getId());
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }
}
