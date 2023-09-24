package ru.practicum.shareit.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.enums.EnumState;
import ru.practicum.shareit.booking.model.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ExceptionsHandler;
import ru.practicum.shareit.request.repo.RequestRepository;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class BookingControllerTest {
    private MockMvc mvc;
    @Autowired
    private BookingController bookingController;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private BookingService bookingService;
    @Autowired
    private RequestRepository requestRepository;

    @BeforeEach
    void setUp() {
        objectMapper.findAndRegisterModules();
        mvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .setControllerAdvice(ExceptionsHandler.class)
                .build();
    }

    @Test
    void create() throws Exception {
        //before
        BookingDtoResponse bookingDtoResponse = Instancio.create(BookingDtoResponse.class);
        BookingDtoRequest bookingDtoRequest = Instancio.of(BookingDtoRequest.class)
                .set(field(BookingDtoRequest::getEnd), LocalDateTime.now().plusDays(6))
                .set(field(BookingDtoRequest::getStart), LocalDateTime.now().plusDays(1))
                .create();
        Long userId = 5L;
        when(bookingService.saveBooking(eq(bookingDtoRequest), eq(userId))).thenReturn(bookingDtoResponse);
        //when
        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDtoRequest))
                        .header("X-Sharer-User-Id", 5L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$.id", is(bookingDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDtoResponse.getStatus().name()), String.class));
    }

    @Test
    void approveOrRejectBooking() throws Exception {
        //before
        BookingDtoResponse bookingDtoResponse = Instancio.create(BookingDtoResponse.class);
        Long bookingId = 5L;
        Long userId = 100L;
        Boolean approved = true;
        when(bookingService.approveOrRejectBooking(eq(bookingId), eq(userId), eq(approved))).thenReturn(bookingDtoResponse);
        //when
        mvc.perform(patch("/bookings/{bookingId}?approved={approved}", 5L, true)
                        .header("X-Sharer-User-Id", 100L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$.id", is(bookingDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDtoResponse.getStatus().name()), String.class));
    }

    @Test
    void findBookingById() throws Exception {
        //before
        BookingDtoResponse bookingDtoResponse = Instancio.create(BookingDtoResponse.class);
        Long bookingId = 5L;
        Long userId = 100L;
        when(bookingService.findBookingById(eq(bookingId), eq(userId))).thenReturn(bookingDtoResponse);
        //when
        mvc.perform(get("/bookings/{bookingId}", 5L, true)
                        .header("X-Sharer-User-Id", 100L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$.id", is(bookingDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDtoResponse.getStatus().name()), String.class));
    }

    @Test
    void findUsersBookings() throws Exception {
        //before
        Pageable pageable = PageRequest.of(0, 10);
        List<BookingDtoResponse> bookingDtoResponseList = Instancio.ofList(BookingDtoResponse.class)
                .size(4)
                .create();
        when(bookingService.findUsersBookings(eq(1L), eq(EnumState.ALL), eq(pageable))).thenReturn(bookingDtoResponseList);
        //when
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].id", is(bookingDtoResponseList.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDtoResponseList.get(0).getStatus().name()), String.class));
    }

    @Test
    void findOwnersBookings() throws Exception {
        //before
        Pageable pageable = PageRequest.of(0, 10);
        List<BookingDtoResponse> bookingDtoResponseList = Instancio.ofList(BookingDtoResponse.class)
                .size(4)
                .create();
        when(bookingService.findOwnersBookings(eq(1L), eq(EnumState.ALL), eq(pageable))).thenReturn(bookingDtoResponseList);
        //when
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].id", is(bookingDtoResponseList.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDtoResponseList.get(0).getStatus().name()), String.class));
    }
}