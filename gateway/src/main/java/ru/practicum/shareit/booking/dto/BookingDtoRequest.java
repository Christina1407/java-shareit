package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.OnCreate;
import ru.practicum.shareit.valid.StartBeforeEndDateValid;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@StartBeforeEndDateValid
@EqualsAndHashCode
@Builder
public class BookingDtoRequest {
    @NotNull(message = "startDate is null", groups = OnCreate.class)
    @FutureOrPresent(message = "startDate can't be in past")
    private LocalDateTime start;
    @NotNull(message = "endDate is null", groups = OnCreate.class)
    @Future(message = "endDate can't be in past")
    private LocalDateTime end;
    @NotNull(message = "itemId is null", groups = OnCreate.class)
    private Long itemId;
}
