package com.softserve.itacademy.dto;

import com.softserve.itacademy.model.Task;
import lombok.Value;

@Value
public class TaskResponseDto {
    private long id;

    private String name;

    private String priority;

    private long todoId;

    private long stateId;

    public TaskResponseDto(Task task){
        this.id= task.getId();
        this.name= task.getName();
        this.priority=task.getPriority().toString();
        this.stateId=task.getState().getId();
        this.todoId=task.getTodo().getId();
    }
}
