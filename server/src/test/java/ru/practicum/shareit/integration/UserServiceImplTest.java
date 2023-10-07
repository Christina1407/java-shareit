package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        UserDto user2 = userService.saveUser(UserDto.builder()
                .name("test2")
                .email("test2@test.ru")
                .build());

        //when
        List<UserDto> allUsers = userService.getAllUsers();
        //then
        assertThat(allUsers.size()).isEqualTo(2);
        List<UserDto> userDtos = new ArrayList<>(List.of(userDto, user2));
        assertThat(userDtos)
                .usingRecursiveComparison()
                .isEqualTo(allUsers);
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
        userService.deleteUser(1L);
        //then
        assertThatThrownBy(() -> userService.findUserById(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateUser() {
        //before
        userDto.setName("updateName");
        userDto.setEmail("updateemail@test.ru");
        //when
        UserDto userById = userService.findUserById(1L);
        assertThat(userById.getEmail()).isEqualTo("test@test.ru");
        assertThat(userById.getName()).isEqualTo("test");
        userService.updateUser(userDto);
        UserDto userById1 = userService.findUserById(1L);
        //then
        assertThat(userById1.getName()).isEqualTo("updateName");
        assertThat(userById1.getEmail()).isEqualTo("updateemail@test.ru");
        assertThat(userById1.getId()).isEqualTo(1L);
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