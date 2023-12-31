package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.OnCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoRequest {
    private static final int MAX_LENGTH_DESCRIPTION = 200;
    private Long id;
    @NotBlank(message = "name is empty", groups = OnCreate.class)
    @Size(max = MAX_LENGTH_DESCRIPTION, message = "name is more than 200 symbols")
    private String name;
    @Size(max = MAX_LENGTH_DESCRIPTION, message = "description is more than 200 symbols")
    @NotNull(message = "description is null", groups = OnCreate.class)
    private String description;
    @NotNull(message = "available is null", groups = OnCreate.class)
    private Boolean available;
    private Long requestId;
}
