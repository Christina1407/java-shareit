package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDtoRequest {
    @NotBlank(message = "text is empty")
    @Size(max = 2000, message = "name is more than 2000 symbols")
    private String text;
}
