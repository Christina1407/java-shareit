package ru.practicum.shareit.rest.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.ExceptionsHandler;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class UserControllerTest {

    private MockMvc mvc;

    @Autowired
    private UserController userController;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(ExceptionsHandler.class)
                .build();
    }

    @Test
    void findAll() throws Exception {
        //before
        List<UserDto> userList = new ArrayList<>();
        UserDto userDto = UserDto.builder()
                .id(1L)
                .email("test@test.ru")
                .name("test")
                .build();
        userList.add(userDto);
        when(userService.getAllUsers()).thenReturn(userList);
        //when
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void create() {
        //before
        //when
        //then
    }

    @Test
    void deleteUser() {
        //before
        //when
        //then
    }

    @Test
    void update() {
        //before
        //when
        //then
    }

    @Test
    void findUser() {
        //before
        //when
        //then
    }
}