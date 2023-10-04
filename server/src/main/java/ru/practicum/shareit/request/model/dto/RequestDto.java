package ru.practicum.shareit.request.model.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class RequestDto {
    private String description;
}
