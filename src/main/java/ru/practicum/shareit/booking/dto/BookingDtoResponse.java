package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.EnumStatus;
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

    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private EnumStatus status;

    private UserBookingDto booker;

    private ItemBookingDto item;
}
