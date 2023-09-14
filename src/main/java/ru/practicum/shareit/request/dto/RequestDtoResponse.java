package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.comments.model.dto.CommentDtoResponse;
import ru.practicum.shareit.item.model.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class RequestDtoResponse {
    private final Long id;
    private final String description;
    private final LocalDateTime created;
    private List<ItemDto> items;
}
