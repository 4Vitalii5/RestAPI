package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.UserRequestDto;
import com.softserve.itacademy.dto.UserResponseDto;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.RoleService;
import com.softserve.itacademy.service.UserService;
import jdk.dynalink.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;


    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> create(@RequestBody UserRequestDto userRequestDto) {
        log.info("[POST] Request to create user");
        User user = new User();
        user.setFirstName(userRequestDto.getFirstName());
        user.setLastName(userRequestDto.getLastName());
        user.setEmail(userRequestDto.getEmail());
        user.setPassword(userRequestDto.getPassword());
        user.setRole(roleService.readById(2));
        userService.create(user);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(user.getId())
                .toUri();
        return ResponseEntity.created(location).body(new UserResponseDto(user));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN') or authentication.principal.id == #id")
    public UserResponseDto read(@PathVariable long id) {
        log.info("[GET] Request to read user");
        return new UserResponseDto(userService.readById(id));
    }

    @PatchMapping("/{id}/update")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER') and authentication.principal.id == #id")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> update(@PathVariable long id,
                                    @RequestBody UserRequestDto userRequestDto,
                                    Authentication authentication) {
        log.info("[Patch] Request to update user");
        User oldUser = userService.readById(id);
        oldUser.setFirstName(userRequestDto.getFirstName());
        oldUser.setLastName(userRequestDto.getLastName());
        oldUser.setLastName(userRequestDto.getLastName());
        oldUser.setEmail(userRequestDto.getEmail());
        oldUser.setPassword(userRequestDto.getPassword());
        userService.update(oldUser);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id")
                .buildAndExpand(oldUser.getId())
                .toUri();
        return ResponseEntity.created(location).body(new UserResponseDto(oldUser));
    }

    @GetMapping("/{id}/delete")
    @PreAuthorize("hasAuthority('ADMIN') or authentication.principal.id == #id")
    public ResponseEntity delete(@PathVariable("id") long id, Authentication authentication) {
        log.info("[GET] Request to delete user");
        userService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN') or authentication.principal.id == #id")
    public List<UserResponseDto> getAll() {
        log.info("[GET] Request to read all users");
        return userService.getAll().stream()
                .map(UserResponseDto:: new)
                .collect(Collectors.toList());
    }
}
