package ru.practicum.shareit.manager;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

@Component
@AllArgsConstructor
public class UserManager {
    private final UserRepository userRepository;
    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(NotFoundException::new);
    }
}
