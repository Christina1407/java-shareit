package ru.practicum.shareit.item.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.OnCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@Builder
public class ItemDto {
    private static final int MAX_LENGTH_DESCRIPTION = 200;
    private Long id;
    @NotBlank(message = "name is empty", groups = OnCreate.class)
    private final String name;
    @Size(max = MAX_LENGTH_DESCRIPTION, message = "description is more than 200 symbols")
    @NotNull(message = "description is null", groups = OnCreate.class)
    private final String description;
    @NotNull(message = "available is null", groups = OnCreate.class)
    private final Boolean available;
    //private final Integer numberOfRents;
}
