package ru.practicum.shareit.booking.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.enums.EnumStatus;
import ru.practicum.shareit.item.model.dto.ItemBookingDto;
import ru.practicum.shareit.user.UserBookingDto;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
@Builder
public class BookingDtoResponse {

    private final Long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final EnumStatus status;
    private final UserBookingDto booker;
    private final ItemBookingDto item;
}
