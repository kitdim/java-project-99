package hexlet.code.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.TaskService;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TestUtils testUtils;

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
    @Test
    @DisplayName("Test find by assigneeId")
    public void testWithAssignee() throws Exception {
        Long id = taskRepository.findByName(testTask.getName()).get().getAssignee().getId();
        String path = "/api/tasks?assigneeId=" + id;
        MvcResult result = mockMvc.perform(get(path).with(token))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray().allSatisfy(element ->
                assertThatJson(element)
                        .and(v -> v.node("assignee_id").isEqualTo(id))
        );
    }
    @Test
    @DisplayName("Test update by id")
    public void testUpdate() throws Exception {
        TaskUpdateDTO dto = new TaskUpdateDTO();
        dto.setTitle(JsonNullable.of("updated_name"));
        dto.setContent(JsonNullable.of("updated description"));

        MockHttpServletRequestBuilder request = put("/api/tasks/{id}", testTask.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        Task task = taskRepository.findByName("updated_name").get();

        assertThat(task.getName()).isEqualTo("updated_name");
        assertThat(task.getDescription()).isEqualTo("updated description");
        assertThat(task.getAssignee().getUsername()).isEqualTo(testTask.getAssignee().getUsername());
        assertThat(task.getTaskStatus().getSlug()).isEqualTo(testTask.getTaskStatus().getSlug());
    }

    @Test
    @DisplayName("Test delete by id")
    public void testDelete() throws Exception {
        Long id = testTask.getId();
        MockHttpServletRequestBuilder request = delete("/api/tasks/{id}", id).with(token);
        mockMvc.perform(request).andExpect(status().isNoContent());
    }
}
