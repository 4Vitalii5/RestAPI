package com.softserve.itacademy.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExceptionDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime timestamp;
    private int status;
    private String error;

    public ExceptionDto(LocalDateTime timestamp, String error, int status){
        this.timestamp=timestamp;
        this.status=status;
        this.error=error;
    }

    public ExceptionDto(){
    }
}
