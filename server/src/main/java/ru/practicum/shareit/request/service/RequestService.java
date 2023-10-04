package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.dto.RequestDto;
import ru.practicum.shareit.request.model.dto.RequestDtoResponse;

import java.util.List;

public interface RequestService {
    RequestDtoResponse saveRequest(RequestDto requestDto, Long requesterId);

    RequestDtoResponse findRequestById(Long requestId, Long userId);

    List<RequestDtoResponse> findUsersRequests(Long requesterId);

    List<RequestDtoResponse> findRequests(Long userId, Pageable pageable);
}
