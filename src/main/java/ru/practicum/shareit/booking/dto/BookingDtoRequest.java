package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.context.annotation.DependsOn;
import ru.practicum.shareit.OnCreate;
import ru.practicum.shareit.booking.EnumStatus;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class BookingDtoRequest {
    @NotNull(message = "startDate is null", groups = OnCreate.class)
    @FutureOrPresent(message = "startDate can't be in past")
    private LocalDateTime start;
    @NotNull(message = "endDate is null", groups = OnCreate.class)
    @Future(message = "endDate can't be in past")
    private LocalDateTime end;
    private Long bookerId;
    @NotNull(message = "itemId is null", groups = OnCreate.class)
    private Long itemId;
   }
