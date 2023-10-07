package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> findAll() {
        return userService.getAllUsers();
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto user) {
        log.info("Попытка создания нового пользователя {}", user);
        return userService.saveUser(user);
    }

    @DeleteMapping("{userId}")
    public void deleteUser(@PathVariable("userId") Long userId) {
        log.info("Попытка удаления пользователя id = {}", userId);
        userService.deleteUser(userId);
    }

    @PatchMapping("{userId}")
    public UserDto update(@PathVariable("userId") Long userId,
                          @RequestBody UserDto user) {
        log.info("Попытка обновления пользователя id = {}", userId);
        user.setId(userId);
        return userService.updateUser(user);
    }

    @GetMapping("{userId}")
    public UserDto findUser(@PathVariable("userId") Long userId) {
        return userService.findUserById(userId);
    }

}
