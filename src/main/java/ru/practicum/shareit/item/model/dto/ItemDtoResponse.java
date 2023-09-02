package ru.practicum.shareit.item.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.dto.BookingItemDto;

@Data
@AllArgsConstructor
@Builder
public class ItemDtoResponse {

    private final Long id;
    private final String name;
    private final String description;
    private final Boolean available;
    private BookingItemDto lastBooking;
    private BookingItemDto nextBooking;
}
