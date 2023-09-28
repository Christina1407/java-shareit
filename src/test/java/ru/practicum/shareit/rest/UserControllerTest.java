package ru.practicum.shareit.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.ExceptionsHandler;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class UserControllerTest {

    private MockMvc mvc;
    @Captor
    ArgumentCaptor<UserDto> userDtoArgumentCaptor;

    @Autowired
    private UserController userController;
    private final ObjectMapper objectMapper = new ObjectMapper();

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
    void create() throws Exception {
        //before
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("test")
                .email("test@mail.com")
                .build();
        when(userService.saveUser(any())).thenReturn(userDto);

        //when
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class));
    }

    @Test
    void createFail() throws Exception {
        //before
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("test")
                .email("testmail.com")
                .build();
        when(userService.saveUser(any())).thenReturn(userDto);

        //when
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.error", is("Ошибка валидации"), String.class))
                .andExpect(jsonPath("$.errors.email", is("email is not well-formed email address"), String.class));
    }

    @Test
    void deleteUser() throws Exception {
        //before
        //when
        mvc.perform(delete("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void update() throws Exception {
        //before
        UserDto updateUserDto = UserDto.builder()
                .name("test")
                .email("test@mail.com")
                .build();
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("test")
                .email("test@mail.com")
                .build();
        when(userService.updateUser(any())).thenReturn(userDto);

        //when
        mvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(updateUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class));
        verify(userService, only()).updateUser(userDtoArgumentCaptor.capture());
        UserDto userDtoUpdated = userDtoArgumentCaptor.getValue();
        assertThat(userDtoUpdated.getId()).isEqualTo(1L);
    }

    @Test
    void findUser() throws Exception {
        //before
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("test")
                .email("test@mail.com")
                .build();
        when(userService.findUserById(eq(1L))).thenReturn(userDto);

        //when
        mvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class));
    }
}