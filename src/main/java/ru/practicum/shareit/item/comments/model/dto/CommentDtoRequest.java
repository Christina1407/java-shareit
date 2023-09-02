package ru.practicum.shareit.item.comments.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@Builder
public class CommentDtoRequest {
    @NotBlank(message = "text is empty")
    @Size(max = 2000, message = "name is more than 2000 symbols")
    private String text;

}
