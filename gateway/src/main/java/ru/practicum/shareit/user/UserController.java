package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.OnCreate;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
@Validated
@Slf4j
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return userClient.getAllUsers();
    }

    @PostMapping
    @Validated(OnCreate.class)
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto user) {
        log.info("Попытка создания нового пользователя {}", user);
        return userClient.saveUser(user);
    }

    @DeleteMapping("{userId}")
    public void deleteUser(@PathVariable("userId") @Min(1) Long userId) {
        log.info("Попытка удаления пользователя id = {}", userId);
        userClient.deleteUser(userId);
    }

    @PatchMapping("{userId}")
    public ResponseEntity<Object> update(@PathVariable("userId") @Min(1) Long userId,
                          @Valid @RequestBody UserDto user) {
        log.info("Попытка обновления пользователя id = {}", userId);
        return userClient.updateUser(userId, user);
    }

    @GetMapping("{userId}")
    public ResponseEntity<Object> findUser(@PathVariable("userId") @Min(1) Long userId) {
        return userClient.findUserById(userId);
    }

}
