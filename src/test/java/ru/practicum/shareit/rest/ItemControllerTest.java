package ru.practicum.shareit.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.ExceptionsHandler;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemDtoRequest;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ItemControllerTest {
    private MockMvc mvc;
    @Autowired
    private ItemController itemController;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .setControllerAdvice(ExceptionsHandler.class)
                .build();
    }

    @Test
    void create() throws Exception {
        //before
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("test")
                .description("testDescription")
                .available(true)
                .requestId(2L)
                .build();
        when(itemService.saveItem(any(), any())).thenReturn(itemDto);
        ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder()
                .requestId(2L)
                .available(true)
                .description("testDesc")
                .name("testName")
                .build();
        //when
        mvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDtoRequest))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class));
    }

    @Test
    void update() {
    }

    @Test
    void findItemById() {
    }

    @Test
    void findUsersItems() {
    }

    @Test
    void searchItems() {
    }

    @Test
    void createComment() {
    }
}