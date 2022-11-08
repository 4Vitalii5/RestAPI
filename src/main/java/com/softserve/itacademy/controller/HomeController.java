package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.UserRequestDto;
import com.softserve.itacademy.dto.UserResponseDto;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.RoleService;
import com.softserve.itacademy.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class HomeController {
    private final UserService userService;
    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping({"/", "home"})
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponseDto> home(@RequestBody UserRequestDto userRequestDto,
                                Authentication authentication) {
        log.info("Home request");
        User user = userService.readByEmail(userRequestDto.getEmail());
        if(user.getRole().getName().equals("ADMIN")) {
            return userService.getAll().stream()
                    .map(UserResponseDto::new)
                    .collect(Collectors.toList());
        } else {
            return List.of(new UserResponseDto(userService.readByEmail(userRequestDto.getEmail())));
        }
    }
}
