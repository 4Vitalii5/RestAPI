package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.TaskResponseDto;
import com.softserve.itacademy.dto.ToDoRequestDto;
import com.softserve.itacademy.dto.ToDoResponseDto;
import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.TaskService;
import com.softserve.itacademy.service.ToDoService;
import com.softserve.itacademy.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/todos")
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
//    @PreAuthorize("hasAuthority('ADMIN') or authentication.principal.id == #ownerId")
    public ResponseEntity<?> create(@PathVariable("owner_id")Long ownerId,
                                    @RequestBody ToDoRequestDto toDoRequestDto) {
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
//    @PreAuthorize("hasAuthority('ADMIN') or " +
//            "@toDoController.isOwner(authentication.principal.id, #id) or " +
//            "@toDoController.isCollaborator(authentication.principal.id, #id)")
    public ToDoResponseDto read(@PathVariable Long id) {
        return new ToDoResponseDto(todoService.readById(id));
    }

    @PatchMapping("/{todo_id}/update/users/{owner_id}")
//    @PreAuthorize("hasAuthority('ADMIN') or authentication.principal.id == #ownerId")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> update(@PathVariable("todo_id") Long todoId,
                                    @PathVariable("owner_id") Long ownerId,
                                    @RequestBody ToDoRequestDto toDoRequestDto) {
        ToDo toDo = todoService.readById(todoId);
        toDo.setTitle(toDoRequestDto.getTitle());
        todoService.update(toDo);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(toDo.getId())
                .toUri();
        return ResponseEntity.created(location).body(new ToDoResponseDto(toDo));
    }


//    @GetMapping("/{todo_id}/delete/users/{owner_id}")


    @DeleteMapping("/{id}/delete/users/{owner_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @PreAuthorize("hasAuthority('ADMIN') or authentication.principal.id == #ownerId")
    public ResponseEntity<?> delete(@PathVariable Long id,
                                    @PathVariable("owner_id") Long ownerId) {
        todoService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
//    @PreAuthorize("hasAuthority('ADMIN')")
    public List<ToDoResponseDto> getAll() {
        return todoService.getAll()
                .stream()
                .map(ToDoResponseDto::new).collect(Collectors.toList());
    }

    @GetMapping("/{todo_id}/tasks")
    @ResponseStatus(HttpStatus.OK)
    public List<TaskResponseDto> readTasks(@PathVariable("todo_id") Long todoId) {
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
