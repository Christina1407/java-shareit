package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.enums.EnumState;
import ru.practicum.shareit.booking.model.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public BookingDtoResponse create(@RequestBody BookingDtoRequest bookingDtoRequest,
                                     @RequestHeader(USER_ID) Long userId) {
        log.info("Попытка создания нового бронирования {}", bookingDtoRequest);
        return bookingService.saveBooking(bookingDtoRequest, userId);
    }

    @PatchMapping("{bookingId}")
    public BookingDtoResponse approveOrRejectBooking(@PathVariable("bookingId") Long bookingId,
                                                     @RequestHeader(USER_ID) Long userId,
                                                     @RequestParam Boolean approved) {
        log.info("Попытка обновления booking id = {}", bookingId);
        return bookingService.approveOrRejectBooking(bookingId, userId, approved);
    }

    @GetMapping("{bookingId}")
    public BookingDtoResponse findBookingById(@PathVariable("bookingId") Long bookingId,
                                              @RequestHeader(USER_ID) Long userId) {
        return bookingService.findBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDtoResponse> findUsersBookings(@RequestHeader(USER_ID) Long bookerId,
                                                      @RequestParam(name = "state") EnumState state,
                                                      @RequestParam(name = "from") int from,
                                                      @RequestParam(name = "size") int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return bookingService.findUsersBookings(bookerId, state, pageable);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> findOwnersBookings(@RequestHeader(USER_ID) Long ownerId,
                                                       @RequestParam(name = "state") EnumState state,
                                                       @RequestParam(name = "from") int from,
                                                       @RequestParam(name = "size") int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return bookingService.findOwnersBookings(ownerId, state, pageable);
    }
}
