package ru.practicum.shareit.request.model.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class RequestDto {
    private String description;
}
