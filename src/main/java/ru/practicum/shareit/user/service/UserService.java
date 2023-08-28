package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto saveUser(UserDto userDto);

    void deleteUser(Long userId);

    UserDto updateUser(UserDto userDto);

    UserDto findUserById(Long userId);
}
