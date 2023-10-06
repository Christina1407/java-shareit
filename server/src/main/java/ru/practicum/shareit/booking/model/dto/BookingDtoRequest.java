package ru.practicum.shareit.booking.model.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
public class BookingDtoRequest {
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
}
