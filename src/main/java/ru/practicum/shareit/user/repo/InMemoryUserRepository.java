package ru.practicum.shareit.user.repo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
@Slf4j
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long id;

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User saveUser(User user) {
        boolean matchEmail = users.values().stream()
                .anyMatch(userFromMap -> userFromMap.getEmail().equals(user.getEmail()));
        if (matchEmail) {
            log.error("Пользователь с email = {} уже существует", user.getEmail());
            throw new AlreadyExistsException();
        }
        user.setId(getId());
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public void deleteUser(Long userId) {
        users.remove(userId);
    }

    @Override
    public User updateUser(User user) {
        User userForUpdate = users.get(user.getId());
        Optional.ofNullable(user.getName()).ifPresent(name -> userForUpdate.setName(user.getName()));
        boolean matchEmail = users.values().stream()
                .filter(userFromMap -> !userFromMap.getId().equals(user.getId()))
                .anyMatch(userFromMap -> userFromMap.getEmail().equals(user.getEmail()));
        if (matchEmail) {
            log.error("Пользователь с email = {} уже существует", user.getEmail());
            throw new AlreadyExistsException();
        }
        Optional.ofNullable(user.getEmail()).ifPresent(name -> userForUpdate.setEmail(user.getEmail()));
        return userForUpdate;
    }

    @Override
    public User findUserById(Long userId) {
        if (!users.containsKey(userId)) {
            log.error("Пользователь c id = {} не найден", userId);
            throw new NotFoundException();
        }
        return users.get(userId);
    }

    private long getId() {
        return ++id;
    }
}
