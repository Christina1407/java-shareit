package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.OnCreate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Long id;
    @NotBlank(message = "email is empty", groups = OnCreate.class)
    @Email(message = "email is not well-formed email address")
    @Size(max = 320, message = "email is more than 320 symbols")
    private String email;
    @NotBlank(message = "name is empty", groups = OnCreate.class)
    @Size(max = 200, message = "name is more than 200 symbols")
    private String name;
}
