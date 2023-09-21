package ru.practicum.shareit.item.comments.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor(force = true)
public class CommentDtoResponse {
    private final Long id;
    private final String text;
    private final String authorName;
    private final LocalDateTime created;
}
