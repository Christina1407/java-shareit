package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoResponse;

import java.util.List;
@Service
@AllArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService{
    private final RequestRepository requestRepository;
    @Override
    public RequestDtoResponse saveRequest(RequestDto requestDto, Long requesterId) {
        return null;
    }

    @Override
    public RequestDtoResponse findRequestById(Long requestId, Long userId) {
        return null;
    }

    @Override
    public List<RequestDtoResponse> findUsersRequests(Long requesterId) {
        return null;
    }
}
