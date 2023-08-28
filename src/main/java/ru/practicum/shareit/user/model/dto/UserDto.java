package ru.practicum.shareit.user.model.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.OnCreate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {
    private Long id;
    @NotBlank(message = "email is empty", groups = OnCreate.class)
    @Email(message = "email is not well-formed email address")
    private final String email;
    @NotBlank(message = "name is empty", groups = OnCreate.class)
    private final String name;
}
