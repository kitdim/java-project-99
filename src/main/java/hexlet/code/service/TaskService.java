package hexlet.code.service;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskMapper taskMapper;
    private final TaskRepository taskRepository;

    public List<TaskDTO> getAll() {
        return taskRepository.findAll()
                .stream()
                .map(taskMapper::map)
                .toList();
    }
    public TaskDTO findTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not Found: " + id));
        return taskMapper.map(task);
    }

    public TaskDTO createTask(TaskCreateDTO dto) {
        Task task = taskMapper.map(dto);
        taskRepository.save(task);
        return taskMapper.map(task);
    }

    public void updateTask(TaskUpdateDTO dto, Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found " + id));
        taskMapper.update(dto, task);
        taskRepository.save(task);
    }
}
