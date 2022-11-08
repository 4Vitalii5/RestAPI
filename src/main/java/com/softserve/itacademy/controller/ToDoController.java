package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.TaskResponseDto;
import com.softserve.itacademy.dto.ToDoRequestDto;
import com.softserve.itacademy.dto.ToDoResponseDto;
import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.TaskService;
import com.softserve.itacademy.service.ToDoService;
import com.softserve.itacademy.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/todos")
@Slf4j
public class ToDoController {

    private final ToDoService todoService;
    private final UserService userService;
    private final TaskService taskService;

    public ToDoController(ToDoService todoService, UserService userService, TaskService taskService) {
        this.todoService = todoService;
        this.userService = userService;
        this.taskService = taskService;
    }

    @PostMapping("/create/users/{owner_id}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN') or authentication.principal.id == #ownerId")
    public ResponseEntity<?> create(@PathVariable("owner_id")Long ownerId,
                                    @RequestBody ToDoRequestDto toDoRequestDto) {
        log.info("[POST] Request to create todo");
        ToDo toDo = new ToDo();
        toDo.setTitle(toDoRequestDto.getTitle());
        toDo.setOwner(userService.readById(ownerId));
        toDo.setCreatedAt(LocalDateTime.now());
        todoService.create(toDo);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(toDo.getId())
                .toUri();
        return ResponseEntity.created(location).body(new ToDoResponseDto(toDo));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN') or " +
            "@toDoController.isOwner(authentication.principal.id, #id) or " +
            "@toDoController.isCollaborator(authentication.principal.id, #id)")
    public ToDoResponseDto read(@PathVariable Long id) {
        log.info("[GET] Request to read todo");
        return new ToDoResponseDto(todoService.readById(id));
    }

    @PatchMapping("/{id}/update")
    @PreAuthorize("hasAuthority('ADMIN') or @toDoController.isOwner(authentication.principal.id, #id)")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody ToDoRequestDto toDoRequestDto) {
        log.info("[PATCH] Request to update todo");
        ToDo toDo = todoService.readById(id);
        toDo.setTitle(toDoRequestDto.getTitle());
        todoService.update(toDo);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(toDo.getId())
                .toUri();
        return ResponseEntity.created(location).body(new ToDoResponseDto(toDo));
    }

    @DeleteMapping("/{id}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN') or " +
            "@toDoController.isOwner(authentication.principal.id, #id)")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        log.info("[DELETE] Request to create todo");
        todoService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<ToDoResponseDto> getAll() {
        log.info("[GET] Request to read all todos");
        return todoService.getAll()
                .stream()
                .map(ToDoResponseDto::new).collect(Collectors.toList());
    }

    @GetMapping("/{todo_id}/tasks")
    @ResponseStatus(HttpStatus.OK)
    public List<TaskResponseDto> readTasks(@PathVariable("todo_id") Long todoId) {
        log.info("[GET] Request to read tasks in todo");
        return taskService.getByTodoId(todoId)
                .stream()
                .map(TaskResponseDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{todo_id}/users/{user_id}/add")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> addCollaborator(@PathVariable("todo_id") Long todoId,
                                             @PathVariable("user_id") Long userId,
                                             Principal principal) {
        log.info("[GET] Request to  add collaborator");
        User user = userService.readById(userId);
        ToDo todo = todoService.readById(todoId);
        User securityUser = userService.readByEmail(principal.getName());
        if(securityUser.getRole().getName().equals("ADMIN") ||
                securityUser.getId() == todo.getOwner().getId()) {
            if (todo.getCollaborators().contains(user)) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            todo.getCollaborators().add(user);
            todoService.update(todo);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @DeleteMapping("/{todo_id}/users/{user_id}/remove")
    public ResponseEntity<?> removeCollaborator(@PathVariable("todo_id") Long todoId,
                                     @PathVariable("user_id") Long userId,
                                     Principal principal) {
        log.info("[DELETE] Request to remove collaborator");
        ToDo todo = todoService.readById(todoId);
        User securityUser = userService.readByEmail(principal.getName());
        if (securityUser.getRole().getName().equals("ADMIN") ||
                securityUser.getId() == todo.getOwner().getId()) {
            todo.getCollaborators().remove(userService.readById(userId));
            todoService.update(todo);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    public boolean isOwner(long id, long toDoId) {
        return todoService.readById(toDoId).getOwner().getId() == id;
    }

    public boolean isCollaborator(long id, long toDoId) {
        return todoService.readById(toDoId).getCollaborators().contains(userService.readById(id));
    }
}
