package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.OnCreate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@AllArgsConstructor
@Builder
public class RequestDto {
    private static final int MAX_LENGTH_DESCRIPTION = 2000;
    @Size(max = MAX_LENGTH_DESCRIPTION, message = "description is more than 2000 symbols")
    @NotBlank(message = "description is empty", groups = OnCreate.class)
    private final String description;
}
