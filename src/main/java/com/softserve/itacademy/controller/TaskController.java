package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.*;
import com.softserve.itacademy.model.Priority;
import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.StateService;
import com.softserve.itacademy.service.TaskService;
import com.softserve.itacademy.service.ToDoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;
    private final ToDoService todoService;
    private final StateService stateService;

    public TaskController(TaskService taskService, ToDoService todoService, StateService stateService) {
        this.taskService = taskService;
        this.todoService = todoService;
        this.stateService = stateService;
    }

    @PostMapping("/{todo_id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> create(@PathVariable long todo_id, @RequestBody TaskRequestDto taskRequestDto) {
        log.info("[POST] Request to create task");
        Task task = new Task();
        task.setName(taskRequestDto.getName());
        task.setPriority(Priority.valueOf(taskRequestDto.getPriority()));
        task.setTodo(todoService.readById(todo_id));
        task.setState(stateService.readById(5L));
        taskService.create(task);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{todo_id}/{task_id}")
                .buildAndExpand(task.getTodo().getId(),task.getId())
                .toUri();
        return ResponseEntity.created(location).body(new TaskResponseDto(task));
    }

    @GetMapping("/{todo_id}/{task_id}")
    @ResponseStatus(HttpStatus.OK)
//    @PreAuthorize("hasAuthority('ADMIN') or authentication.principal.id == #id")
    public TaskResponseDto read(@PathVariable long todo_id, @PathVariable long task_id) {
        log.info("[GET] Request to read task");
        return new TaskResponseDto(taskService.readById(task_id));
    }

    @GetMapping("/{todo_id}/{task_id}/delete")
//    @PreAuthorize("hasAuthority('ADMIN') or authentication.principal.id == #id")
    public ResponseEntity delete(@PathVariable long todo_id, @PathVariable long task_id, Authentication authentication) {
        log.info("[GET] Request to delete task");
        taskService.delete(task_id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TaskResponseDto> getAll() {
        log.info("[GET] Request to read all tasks");
        return taskService.getAll().stream()
                .map(TaskResponseDto:: new)
                .collect(Collectors.toList());
    }
    @GetMapping("/{todo_id}")
    @ResponseStatus(HttpStatus.OK)
    public List<TaskResponseDto> getAllTodoTask(@PathVariable long todo_id) {
        log.info("[GET] Request to read all tasks for current todo");
        return taskService.getByTodoId(todo_id).stream()
                .map(TaskResponseDto:: new)
                .collect(Collectors.toList());
    }

}
