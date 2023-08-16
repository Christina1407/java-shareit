package ru.practicum.shareit.user.repo;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    List<User> getAllUsers();

    User saveUser(User user);

    void deleteUser(Long userId);

    User updateUser(User user);

    User findUserById(Long userId);
}
