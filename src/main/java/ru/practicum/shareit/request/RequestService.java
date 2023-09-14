package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoResponse;

import java.util.List;

public interface RequestService {
    RequestDtoResponse saveRequest(RequestDto requestDto, Long requesterId);

    RequestDtoResponse findRequestById(Long requestId, Long userId);

    List<RequestDtoResponse> findUsersRequests(Long requesterId);
}
