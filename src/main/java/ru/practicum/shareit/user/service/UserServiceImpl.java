package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.model.dto.UserMapper;
import ru.practicum.shareit.user.repo.UserRepository;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAllUsers() {
        return userMapper.map(userRepository.getAllUsers());
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        User user = userMapper.map(userDto);
        return userMapper.map(userRepository.saveUser(user));
    }

    @Override
    public void deleteUser(Long userId) {
        findUserById(userId);
        userRepository.deleteUser(userId);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        findUserById(userDto.getId());
        User user = userMapper.map(userDto);
        return userMapper.map(userRepository.updateUser(user));
    }

    @Override
    public UserDto findUserById(Long userId) {
        return userMapper.map(userRepository.findUserById(userId));
    }
}
