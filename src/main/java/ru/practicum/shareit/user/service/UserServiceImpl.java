package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.model.dto.UserMapper;
import ru.practicum.shareit.user.repo.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAllUsers() {
        return userMapper.map(userRepository.findAll());
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        User user = userMapper.map(userDto);
        return userMapper.map(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        user.ifPresent(userRepository::delete);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        findUserById(userDto.getId());
        User user = userMapper.map(userDto);
        return userMapper.map(userRepository.save(user));
    }

    @Override
    public UserDto findUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(userMapper::map).orElse(null);
    }
}
