package ru.practicum.shareit.user.model.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public List<UserDto> map(List<User> users) {
        return users.stream()
                .map(this::getBuild)
                .collect(Collectors.toList());
    }

    private UserDto getBuild(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    private User getBuild(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public UserDto map(User user) {
        return getBuild(user);
    }

    public User map(UserDto userDto) {
        return getBuild(userDto);
    }
}
