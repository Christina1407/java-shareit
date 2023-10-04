package ru.practicum.shareit.item.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDtoRequest {
    private static final int MAX_LENGTH_DESCRIPTION = 200;
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}
