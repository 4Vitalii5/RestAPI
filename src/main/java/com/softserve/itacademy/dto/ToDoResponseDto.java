package com.softserve.itacademy.dto;

import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.model.User;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Value
public class ToDoResponseDto {
    String title;
    LocalDateTime createdAt;
    long ownerId;
    List<Long> collaborators;


    public ToDoResponseDto(ToDo toDo) {
        title = toDo.getTitle();
        createdAt = toDo.getCreatedAt();
        ownerId = toDo.getOwner().getId();
        collaborators = toDo.getCollaborators().stream().map(User::getId).collect(Collectors.toList());
    }
}