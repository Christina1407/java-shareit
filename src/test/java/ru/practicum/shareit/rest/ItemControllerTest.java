package ru.practicum.shareit.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.ExceptionsHandler;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.comments.model.dto.CommentDtoRequest;
import ru.practicum.shareit.item.comments.model.dto.CommentDtoResponse;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemDtoRequest;
import ru.practicum.shareit.item.model.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ItemControllerTest {
    private MockMvc mvc;
    @Autowired
    private ItemController itemController;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Captor
    ArgumentCaptor<ItemDtoRequest> itemDtoArgumentCaptor;

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
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class));
    }

    @Test
    void update() throws Exception {
        //before
        ItemDto itemDto = Instancio.create(ItemDto.class);
        ItemDtoRequest itemDtoRequest = Instancio.create(ItemDtoRequest.class);
        Long itemId = 109L;
        Long userId = 1L;
        when(itemService.updateItem(any(), eq(userId))).thenReturn(itemDto);
        //when
        mvc.perform(patch("/items/{itemId}", 109L)
                        .content(objectMapper.writeValueAsString(itemDtoRequest))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class));
        verify(itemService, only()).updateItem(itemDtoArgumentCaptor.capture(), eq(userId));
        ItemDtoRequest finalItemDtoRequest = itemDtoArgumentCaptor.getValue();
        assertThat(finalItemDtoRequest.getId()).isEqualTo(itemId);
    }

    @Test
    void findItemById() throws Exception {
        //before
        ItemDtoResponse itemDtoResponse = Instancio.create(ItemDtoResponse.class);
        Long itemId = 13L;
        Long userId = 1L;
        when(itemService.findItemById(eq(itemId), eq(userId))).thenReturn(itemDtoResponse);
        //when
        mvc.perform(get("/items/{itemId}", 13L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(7))
                .andExpect(jsonPath("$.id", is(itemDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoResponse.getName()), String.class));
    }

    @Test
    void findUsersItems() throws Exception {
        //before
        Long ownerId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<ItemDtoResponse> itemDtoResponseList = Instancio.ofList(ItemDtoResponse.class)
                .size(4)
                .create();
        when(itemService.findUsersItems(eq(ownerId), eq(pageable))).thenReturn(itemDtoResponseList);
        //when
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].id", is(itemDtoResponseList.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoResponseList.get(0).getName()), String.class));
    }

    @Test
    void searchItems() throws Exception {
        //before
        List<ItemDto> itemDtoList = Instancio.ofList(ItemDto.class)
                .size(3)
                .create();
        String text = Instancio.create(String.class);
        Pageable pageable = PageRequest.of(0, 10);
        when(itemService.searchItems(eq(text), eq(1L), eq(pageable))).thenReturn(itemDtoList);
        //when
        mvc.perform(get("/items/search?text={text}", text)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id", is(itemDtoList.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoList.get(0).getName()), String.class));
    }

    @Test
    void createComment() throws Exception {
        //before
        CommentDtoResponse commentDtoResponse = Instancio.create(CommentDtoResponse.class);
        CommentDtoRequest comment = Instancio.create(CommentDtoRequest.class);
        Long itemId = 1L;
        Long userId = 2L;
        when(itemService.saveComment(eq(comment), eq(itemId), eq(userId))).thenReturn(commentDtoResponse);
        //when
        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .content(objectMapper.writeValueAsString(comment))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$.id", is(commentDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDtoResponse.getText()), String.class));
    }
}