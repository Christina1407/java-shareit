package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.OnCreate;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
@Validated
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> findAll() {
        return userService.getAllUsers();
    }

    @PostMapping
    @Validated(OnCreate.class)
    public UserDto create(@Valid @RequestBody UserDto user) {
        log.info("Попытка создания нового пользователя {}", user);
        return userService.saveUser(user);
    }

    @DeleteMapping("{userId}")
    public void deleteUser(@PathVariable("userId") @Min(1) Long userId) {
        log.info("Попытка удаления пользователя id = {}", userId);
        userService.deleteUser(userId);
    }

    @PatchMapping("{userId}")
    public UserDto update(@PathVariable("userId") @Min(1) Long userId,
                          @Valid @RequestBody UserDto user) {
        log.info("Попытка обновления пользователя id = {}", userId);
        user.setId(userId);
        return userService.updateUser(user);
    }

    @GetMapping("{userId}")
    public UserDto findUser(@PathVariable("userId") @Min(1) Long userId) {
        return userService.findUserById(userId);
    }

}
