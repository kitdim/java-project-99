package hexlet.code.api;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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

@SpringBootTest
@AutoConfigureMockMvc
public final class UserControllerTest extends BaseTest {
    @Autowired
    private UserRepository repository;
    @Autowired
    private ModelGenerator modelGenerator;
    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = Instancio.of(modelGenerator.getUserModel())
                .create();
        repository.save(testUser);
    }
    @Test
    public void testIndex() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());
        Optional<User> isHasAdmin = repository.findByEmail("hexlet@example.com").stream().findFirst();
        assertTrue(isHasAdmin.isPresent());
    }
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void testCreate(boolean isSuccess) throws Exception {
        long id = 15L;
        if (isSuccess) {
            repository.save(testUser);
            id = testUser.getId();
        }
        MvcResult result = mockMvc.perform(get("/api/users/{id}", id))
                .andDo(print())
                .andReturn();
        if (isSuccess) {
            UserDto dto = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
            assertEquals(testUser.getFirstName(), dto.getFirstName());
            assertEquals(testUser.getLastName(), dto.getLastName());
        } else {
            assertEquals(404, result.getResponse().getStatus());
        }
    }
    @ParameterizedTest
    @ValueSource(strings = {"valid", "invalidValue"})
    public void testCreate(String result) throws Exception {
        User data = Instancio.of(modelGenerator.getUserModel())
                .create();
        if (result.equals("invalidValue")) {
            data.setEmail("test");
            data.setPassword("1");
        }
        MockHttpServletRequestBuilder request = post("/api/users")
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
        data.put("firstName", "Ivan");
        long id = testUser.getId();
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
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));

        switch (result) {
            case "ok":
                mockMvc.perform(request)
                        .andExpect(status().isOk());
                User user = repository.findById(testUser.getId()).get();
                assertThat(user.getFirstName()).isEqualTo(("Ivan"));
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
        repository.save(testUser);
        long id = testUser.getId();
        MockHttpServletRequestBuilder request = delete("/api/users/{id}", id);

        mockMvc.perform(request).andExpect(status().isNoContent());
        assertFalse(repository.findById(id).isPresent());
    }
}
