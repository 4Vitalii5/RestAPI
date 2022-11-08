package com.softserve.itacademy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ToDoRequestDto {

    private String title;
    @JsonProperty("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
