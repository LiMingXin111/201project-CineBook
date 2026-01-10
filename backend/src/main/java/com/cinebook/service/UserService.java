package com.cinebook.service;

import com.cinebook.dao.UserDao;
import com.cinebook.model.User;

import java.util.List;

public class UserService {
    private final UserDao userDao = new UserDao();

    public List<User> getAllUsers() {
        return userDao.getAll();
    }

    public User getById(int id) {
        return userDao.getById(id);
    }

    public User getByUsername(String username) {
        return userDao.getByUsername(username);
    }

    public User addUser(User user) {
        return userDao.insert(user);
    }

    public boolean usernameExists(String username) {
        return userDao.existsByUsername(username);
    }
}
