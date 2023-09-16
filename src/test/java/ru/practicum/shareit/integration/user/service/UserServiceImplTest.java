package ru.practicum.shareit.integration.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userService;
    private UserDto user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = UserDto.builder()
                .name("test")
                .email("test@test.ru")
                .build();
        userDto = userService.saveUser(user);
    }

    @Test
    void getAllUsers() {
        //before
        //when
        //then
    }

    @Test
    void saveUser() {
        //before
        //when
        //then
        assertThat(userDto).isNotNull();
        assertThat(userDto.getId()).isNotNull();
        assertThat(userDto.getName()).isEqualTo(user.getName());
        assertThat(userDto.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void deleteUser() {
        //before
        //when
        //then
    }

    @Test
    void updateUser() {
        //before
        //when
        //then
    }

    @Test
    void findUserById() {
        //before
        //when
        UserDto findedUserDto = userService.findUserById(userDto.getId());
        //then
        assertThat(findedUserDto).usingRecursiveComparison()
                .isEqualTo(userDto);
    }
}