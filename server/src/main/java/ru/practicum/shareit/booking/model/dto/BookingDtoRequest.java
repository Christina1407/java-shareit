package ru.practicum.shareit.booking.model.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class BookingDtoRequest {
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
}
