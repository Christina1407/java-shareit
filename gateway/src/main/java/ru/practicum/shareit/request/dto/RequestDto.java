package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.OnCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class RequestDto {
    @Size(max = 2000, message = "description is more than 2000 symbols")
    @NotBlank(message = "description is empty", groups = OnCreate.class)
    private String description;
}
