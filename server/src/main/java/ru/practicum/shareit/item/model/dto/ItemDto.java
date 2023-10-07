package ru.practicum.shareit.item.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemDto {
    private final Long id;
    private final String name;
    private final String description;
    private final Boolean available;
    private Long requestId;
}
