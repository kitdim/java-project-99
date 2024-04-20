package hexlet.code.controller;

import hexlet.code.dto.status.TaskStatusCreateDTO;
import hexlet.code.dto.status.TaskStatusDTO;
import hexlet.code.service.TaskStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TaskStatusController {
    private final TaskStatusService taskStatusService;

    @GetMapping(value = "/tasks")
    private ResponseEntity<List<TaskStatusDTO>> index() {
        List<TaskStatusDTO> tasks = taskStatusService.getAll();
        return ResponseEntity
                .ok()
                .header("X-Total-Count", String.valueOf(tasks.size()))
                .body(tasks);
    }

    @GetMapping(value = "/tasks/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskStatusDTO show(@PathVariable Long id) {
        return taskStatusService.findTaskStatus(id);
    }

    @PostMapping(value = "/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatusDTO create(@Valid @RequestBody TaskStatusCreateDTO data) {
        return taskStatusService.createTaskStatus(data);
    }
}
