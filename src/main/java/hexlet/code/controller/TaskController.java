package hexlet.code.controller;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.model.Task;
import hexlet.code.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TaskController {
    private final TaskService taskService;

    @GetMapping(value = "/tasks")
    public ResponseEntity<List<TaskDTO>> index() {
        List<TaskDTO> tasks = taskService.getAll();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(tasks.size()))
                .body(tasks);
    }

    @GetMapping(value = "/tasks/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskDTO show(@PathVariable Long id) {
        return taskService.findTask(id);
    }

    @PostMapping(value = "/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDTO create(@Valid @RequestBody TaskCreateDTO dto) {
        return taskService.createTask(dto);
    }

    @PutMapping(value = "/tasks/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@Valid @RequestBody TaskUpdateDTO dto, @PathVariable Long id) {
        taskService.updateTask(dto, id);
    }
}
