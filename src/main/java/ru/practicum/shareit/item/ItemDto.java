package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
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
    @NotBlank(message = "name is empty")
    private final String name;
    @Size(max = MAX_LENGTH_DESCRIPTION, message = "description is more than 200 symbols")
    @NotBlank(message = "description is empty")
    private final String description;
    @NotBlank(message = "available is empty")
    private final Boolean available;
    //private final Integer numberOfRents;
}
