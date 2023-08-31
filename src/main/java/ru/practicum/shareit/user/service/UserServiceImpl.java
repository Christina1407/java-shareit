package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.model.dto.UserMapper;
import ru.practicum.shareit.user.repo.UserRepository;

import java.util.List;
import java.util.Objects;
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
        User user = checkUser(userId);
        userRepository.delete(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        User userForUpdate = checkUser(userDto.getId());
        preUpdate(userForUpdate, userDto);
        return userMapper.map(userRepository.save(userForUpdate));
    }

    @Override
    public UserDto findUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(userMapper::map).orElseThrow(NotFoundException::new);
    }
    private void preUpdate(User userForUpdate, UserDto userDto) {
        if (Objects.nonNull(userDto.getName()) && !userDto.getName().isBlank()) { // если имя пустое, то оно не меняется
            userForUpdate.setName(userDto.getName());
        }

        Optional.ofNullable(userDto.getEmail()).ifPresent(email -> userForUpdate.setEmail(userDto.getEmail()));
    }
    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(NotFoundException::new);
    }
}
