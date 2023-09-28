package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.OnCreate;
import ru.practicum.shareit.booking.enums.EnumState;
import ru.practicum.shareit.booking.model.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingService bookingService;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public BookingDtoResponse create(@Validated(OnCreate.class) @Valid @RequestBody BookingDtoRequest bookingDtoRequest,
                                     @RequestHeader(USER_ID) @Min(1) Long userId) {
        log.info("Попытка создания нового бронирования {}", bookingDtoRequest);
        return bookingService.saveBooking(bookingDtoRequest, userId);
    }

    @PatchMapping("{bookingId}")
    public BookingDtoResponse approveOrRejectBooking(@PathVariable("bookingId") @Min(1) Long bookingId,
                                                     @RequestHeader(USER_ID) @NotNull @Min(1) Long userId,
                                                     @RequestParam @NotNull Boolean approved) {
        log.info("Попытка обновления booking id = {}", bookingId);
        return bookingService.approveOrRejectBooking(bookingId, userId, approved);
    }

    @GetMapping("{bookingId}")
    public BookingDtoResponse findBookingById(@PathVariable("bookingId") @Min(1) Long bookingId,
                                              @RequestHeader(USER_ID) @NotNull @Min(1) Long userId) {
        return bookingService.findBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDtoResponse> findUsersBookings(@RequestHeader(USER_ID) @NotNull @Min(1) Long bookerId,
                                                      @RequestParam(name = "state", defaultValue = "ALL") EnumState state,
                                                      @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                                      @RequestParam(name = "size", defaultValue = "10") @Min(1) int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return bookingService.findUsersBookings(bookerId, state, pageable);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> findOwnersBookings(@RequestHeader(USER_ID) @NotNull @Min(1) Long ownerId,
                                                       @RequestParam(name = "state", defaultValue = "ALL") EnumState state,
                                                       @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                                       @RequestParam(name = "size", defaultValue = "10") @Min(1) int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return bookingService.findOwnersBookings(ownerId, state, pageable);
    }
}
