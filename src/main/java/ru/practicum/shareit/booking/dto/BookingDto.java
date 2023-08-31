package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.OnCreate;
import ru.practicum.shareit.booking.EnumStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
@Builder
public class BookingDto {

    private Long id;
    @NotNull(message = "startDate is null", groups = OnCreate.class)
    private LocalDateTime startDate;
    @NotNull(message = "endDate is null", groups = OnCreate.class)
    private LocalDateTime endDate;
    @NotBlank(message = "status is empty", groups = OnCreate.class)
    @Size(max = 20, message = "status is more than 20 symbols")
    private EnumStatus status;
    @NotNull(message = "bookerId is null", groups = OnCreate.class)
    private Long bookerId;
    @NotNull(message = "itemId is null", groups = OnCreate.class)
    private Long itemId;
    @NotBlank(message = "itemName is empty", groups = OnCreate.class)
    @Size(max = 200, message = "status is more than 200 symbols")
    private String itemName;
}
