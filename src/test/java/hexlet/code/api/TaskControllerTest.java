package hexlet.code.api;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.TaskService;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest extends BaseTest {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private TaskService taskService;
    @Autowired
    private UserRepository userRepository;

    private JwtRequestPostProcessor token;
    private Task testTask;
    private TaskStatus testTaskStatus;
    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = Instancio.of(testUtils.getUserModel()).create();
        userRepository.save(testUser);
        token = jwt().jwt(builder -> builder.subject(testUser.getUsername()));

        testTaskStatus = Instancio.of(testUtils.getTaskStatusModel()).create();
        taskStatusRepository.save(testTaskStatus);

        testTask = Instancio.of(testUtils.getTaskModel()).create();
        testTask.setTaskStatus(testTaskStatus);
        testTask.setAssignee(testUser);
        taskRepository.save(testTask);
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
        assertThatJson(body).isArray();
    }

    @Test
    @DisplayName("Test find by id")
    public void testShow() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/tasks/{id}", testTask.getId()).with(token))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        assertThatJson(body)
                .and(
                        v -> v.node("title").isEqualTo(testTask.getName()),
                        v -> v.node("content").isEqualTo(testTask.getDescription()));
    }
    @Test
    @DisplayName("Test create the task")
    public void testCreate() throws Exception {
        TaskCreateDTO dto = new TaskCreateDTO();
        dto.setTitle("task_name");
        dto.setContent("task description");
        dto.setAssigneeId(testUser.getId());
        dto.setStatus(testTaskStatus.getSlug());

        MockHttpServletRequestBuilder request = post("/api/tasks")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        Task testTask = taskRepository.findByName(dto.getTitle()).get();

        assertThat(testTask).isNotNull();
        assertThat(testTask.getName()).isEqualTo(dto.getTitle());
        assertThat(testTask.getDescription()).isEqualTo(dto.getContent());
    }
}
