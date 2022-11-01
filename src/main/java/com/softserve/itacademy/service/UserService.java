package com.softserve.itacademy.service;

import com.softserve.itacademy.model.User;

import java.util.List;

public interface UserService {
    User create(User user);
    User readById(long id);
    User readByEmail(String email);
    User update(User user);
    void delete(long id);
    List<User> getAll();

}
