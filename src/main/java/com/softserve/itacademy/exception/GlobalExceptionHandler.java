package com.softserve.itacademy.exception;

import com.softserve.itacademy.dto.ExceptionDto;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("Handler 'handleMethodArgumentNotValid' catch 'MethodArgumentNotValidException'");
        ExceptionDto exception = new ExceptionDto(
                LocalDateTime.now(), ex.getMessage(), 409
        );

        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullEntityReferenceException.class)
    public ResponseEntity<ExceptionDto> handleNullEntityReferenceException(NullEntityReferenceException ex, WebRequest request) {
        log.error("Handler 'handleNullEntityReferenceException' catch 'NullEntityReferenceException'");
        ExceptionDto exception = new ExceptionDto(
                LocalDateTime.now(), ex.getMessage(), HttpStatus.BAD_REQUEST.value()
        );

        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionDto> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        log.error("Handler 'handleEntityNotFoundException' catch 'EntityNotFoundException'");
        ExceptionDto exception = new ExceptionDto(
                LocalDateTime.now(), ex.getMessage(), HttpStatus.NOT_FOUND.value()
        );

        return new ResponseEntity<>(exception, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDto> internalServerErrorHandler(Exception ex, WebRequest request) {
        log.error("Handler 'internalServerErrorHandler' catch 'Exception'");
        ExceptionDto exception = new ExceptionDto(
                LocalDateTime.now(), ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()
        );

        return new ResponseEntity<>(exception, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public void constraintViolationException(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }
}