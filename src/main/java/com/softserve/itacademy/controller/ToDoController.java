package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.ToDoRequestDto;
import com.softserve.itacademy.dto.ToDoResponseDto;
import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.ToDoService;
import com.softserve.itacademy.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/todos")
public class ToDoController {

    private final ToDoService todoService;
    private final UserService userService;

    public ToDoController(ToDoService todoService, UserService userService) {
        this.todoService = todoService;
        this.userService = userService;
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

    @GetMapping("/{id}/add")
    public String addCollaborator(@PathVariable long id, @RequestParam("user_id") long userId/*, Principal principal*/) {
        ToDo todo = todoService.readById(id);
//        User securityUser = userService.readByEmail(principal.getName());
//        if(securityUser.getRole().getName().equals("ADMIN") ||
//            securityUser.getId() == todo.getOwner().getId()) {
            List<User> collaborators = todo.getCollaborators();
            collaborators.add(userService.readById(userId));
            todo.setCollaborators(collaborators);
            todoService.update(todo);
            return "redirect:/todos/" + id + "/tasks";
//        }
//        return "redirect:/accessDenied";
    }

    @GetMapping("/{id}/remove")
    public String removeCollaborator(@PathVariable long id, @RequestParam("user_id") long userId/*, Principal principal*/) {
        ToDo todo = todoService.readById(id);
//        User securityUser = userService.readByEmail(principal.getName());
//        if (securityUser.getRole().getName().equals("ADMIN") ||
//                securityUser.getId() == todo.getOwner().getId()) {
            List<User> collaborators = todo.getCollaborators();
            collaborators.remove(userService.readById(userId));
            todo.setCollaborators(collaborators);
            todoService.update(todo);
            return "redirect:/todos/" + id + "/tasks";
//        }
//        return "redirect:/accessDenied";
    }

    public boolean isOwner(long id, long toDoId) {
        return todoService.readById(toDoId).getOwner().getId() == id;
    }

    public boolean isCollaborator(long id, long toDoId) {
        return todoService.readById(toDoId).getCollaborators().contains(userService.readById(id));
    }
}
