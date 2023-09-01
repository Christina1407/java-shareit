package ru.practicum.shareit.booking;


import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

public interface BookingService {
    BookingDtoResponse saveBooking(BookingDtoRequest bookingDtoRequest, Long ownerId);

    BookingDtoResponse approveOrRejectBooking(Long bookingId, Long userId, Boolean approved);
    BookingDtoResponse findBookingById(Long bookingId, Long userId);

    List<BookingDtoResponse> findUsersBookings(Long bookerId, EnumState state);
}
