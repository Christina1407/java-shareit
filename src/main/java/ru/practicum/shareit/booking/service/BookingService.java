package ru.practicum.shareit.booking.service;


import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.enums.EnumState;
import ru.practicum.shareit.booking.model.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.dto.BookingDtoResponse;

import java.util.List;

public interface BookingService {
    BookingDtoResponse saveBooking(BookingDtoRequest bookingDtoRequest, Long ownerId);

    BookingDtoResponse approveOrRejectBooking(Long bookingId, Long userId, Boolean approved);

    BookingDtoResponse findBookingById(Long bookingId, Long userId);

    List<BookingDtoResponse> findUsersBookings(Long bookerId, EnumState state, Pageable pageable);

    List<BookingDtoResponse> findOwnersBookings(Long ownerId, EnumState state, Pageable pageable);
}
