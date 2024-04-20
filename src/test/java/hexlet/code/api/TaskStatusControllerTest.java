package hexlet.code.api;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.dto.status.TaskStatusDTO;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskStatusRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskStatusControllerTest extends BaseTest {
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    private JwtRequestPostProcessor token;
    private TaskStatus testTaskStatus;
    private User testUser;

    @BeforeEach
    public void setUp() {
        testTaskStatus = Instancio.of(testUtils.getTaskStatusModel()).create();
        testUser = Instancio.of(testUtils.getUserModel()).create();
        token = jwt().jwt(builder -> builder.subject(testUser.getUsername()));
        taskStatusRepository.save(testTaskStatus);
    }

    @AfterEach
    public void clean() {
        testUtils.clean();
    }

    @Test
    @DisplayName("Test find all tasks")
    public void testIndex() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/tasks").with(token))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        List<TaskStatus> taskStatuses = objectMapper.readValue(body, new TypeReference<List<TaskStatus>>() {
        });
        List<TaskStatus> expected = taskStatusRepository.findAll();
        assertThat(taskStatuses).containsAll(expected);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @DisplayName("Test find by id")
    public void testShow(boolean isCorrectId) throws Exception {
        Long id;
        if (isCorrectId) {
            id = testTaskStatus.getId();
        } else {
            id = 55L;
        }
        MvcResult result = mockMvc.perform(get("/api/tasks/{id}", id).with(token))
                .andDo(print())
                .andReturn();
        if (isCorrectId) {
            TaskStatusDTO dto = objectMapper.readValue(result.getResponse().getContentAsString(), TaskStatusDTO.class);
            assertEquals(testTaskStatus.getName(), dto.getName());
            assertEquals(testTaskStatus.getSlug(), dto.getSlug());
        } else {
            assertEquals(404, result.getResponse().getStatus());
        }
    }
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @DisplayName("Test create task status")
    public void testCreate(boolean isSuccess) throws Exception {
        TaskStatus someTaskStatus = Instancio.of(testUtils.getTaskStatusModel()).create();
        if (!isSuccess) {
            someTaskStatus.setName("");
            someTaskStatus.setName("");
        }

        MockHttpServletRequestBuilder request = post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(someTaskStatus)).with(token);
        if (isSuccess) {
            someTaskStatus.setId(5L);
            mockMvc.perform(request)
                    .andExpect(status().isCreated());
            TaskStatus taskStatus = taskStatusRepository.findById(someTaskStatus.getId()).get();
            assertNotNull(taskStatus);
            assertThat(someTaskStatus).isEqualTo(taskStatus);
        } else {
            mockMvc.perform(request)
                    .andExpect(status().isBadRequest());
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @DisplayName("Test update the task status")
    public void testUpdate(boolean isSuccess) throws Exception {
        Long id = testTaskStatus.getId();
        HashMap<String, String> data = new HashMap<>();
        data.put("name", "ops");

        if (!isSuccess) {
            id = testTaskStatus.getId();
            data.put("slug", "");
        }
        MockHttpServletRequestBuilder request = put("/api/tasks/{id}", id)
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));
        if (isSuccess) {
            mockMvc.perform(request).andExpect(status().isOk());
            TaskStatus taskStatus = taskStatusRepository.findById(testTaskStatus.getId()).get();
            assertThat(taskStatus.getName()).isEqualTo("ops");
        } else {
            mockMvc.perform(request).andExpect(status().isBadRequest());
        }
    }
    @Test
    @DisplayName("Delete by id")
    public void deleteTest() throws Exception {
        Long id = testTaskStatus.getId();
        MockHttpServletRequestBuilder request = delete("/api/tasks/{id}", id).with(token);
        mockMvc.perform(request).andExpect(status().isNoContent());
    }

}
