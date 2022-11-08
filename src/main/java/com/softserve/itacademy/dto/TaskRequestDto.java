package com.softserve.itacademy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class TaskRequestDto {

        @JsonProperty("task_name")
        private String name;

        @JsonProperty("priority")
        private String priority;

}
