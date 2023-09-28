package ru.practicum.shareit.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.ExceptionsHandler;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.model.dto.RequestDto;
import ru.practicum.shareit.request.model.dto.RequestDtoResponse;
import ru.practicum.shareit.request.service.RequestService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ItemRequestControllerTest {
    private MockMvc mvc;
    @Autowired
    private ItemRequestController itemRequestController;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private RequestService requestService;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemRequestController)
                .setControllerAdvice(ExceptionsHandler.class)
                .build();
    }

    @Test
    void create() throws Exception {
        //before
        RequestDtoResponse requestDtoResponse = Instancio.create(RequestDtoResponse.class);
        RequestDto requestDto = Instancio.create(RequestDto.class);
        when(requestService.saveRequest(eq(requestDto), eq(1L))).thenReturn(requestDtoResponse);
        //when
        mvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$.id", is(requestDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDtoResponse.getDescription()), String.class));
    }

    @Test
    void findRequestById() throws Exception {
        //before
        RequestDtoResponse requestDtoResponse = Instancio.create(RequestDtoResponse.class);
        Long requestId = 10L;
        Long userId = 1L;
        when(requestService.findRequestById(eq(requestId), eq(userId))).thenReturn(requestDtoResponse);
        //when
        mvc.perform(get("/requests/{requestId}", 10L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$.id", is(requestDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDtoResponse.getDescription()), String.class));
    }

    @Test
    void findUsersRequests() throws Exception {
        //before
        List<RequestDtoResponse> responseList = Instancio.ofList(RequestDtoResponse.class)
                .size(5)
                .create();
        Long requesterId = 10L;
        when(requestService.findUsersRequests(eq(requesterId))).thenReturn(responseList);
        //when
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 10L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].id", is(responseList.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(responseList.get(0).getDescription()), String.class));
    }

    @Test
    void findRequests() throws Exception {
        //before
        List<RequestDtoResponse> responseList = Instancio.ofList(RequestDtoResponse.class)
                .size(3)
                .create();
        Long userId = 10L;
        when(requestService.findRequests(eq(userId), eq(PageRequest.of(0, 10)))).thenReturn(responseList);
        //when
        mvc.perform(get("/requests/all?from={from}&size={size}", 0, 10)
                        .header("X-Sharer-User-Id", 10L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id", is(responseList.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(responseList.get(0).getDescription()), String.class));

    }
}