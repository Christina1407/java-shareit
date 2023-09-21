package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.manager.UserManager;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.model.dto.RequestDto;
import ru.practicum.shareit.request.model.dto.RequestDtoResponse;
import ru.practicum.shareit.request.model.dto.RequestMapper;
import ru.practicum.shareit.request.repo.RequestRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserManager userManager;
    private final RequestMapper requestMapper;

    @Override
    public RequestDtoResponse saveRequest(RequestDto requestDto, Long requesterId) {
        User user = userManager.findUserById(requesterId);
        Request save = requestRepository.save(requestMapper.map(requestDto, user));
        return requestMapper.map(save, false);
    }

    @Override
    public RequestDtoResponse findRequestById(Long requestId, Long userId) {
        userManager.findUserById(userId);
        Request request = requestRepository.findById(requestId).orElseThrow(NotFoundException::new);
        return requestMapper.map(request, true);
    }

    @Override
    public List<RequestDtoResponse> findUsersRequests(Long requesterId) {
        userManager.findUserById(requesterId);
        List<Request> requests = requestRepository.findByRequester_IdOrderByCreatedDateDesc(requesterId);
        return requestMapper.map(requests, true);
    }

    @Override
    public List<RequestDtoResponse> findRequests(Long userId, Pageable pageable) {
        userManager.findUserById(userId);
        List<Request> requests = requestRepository.findByRequester_IdNotOrderByCreatedDateDesc(userId, pageable);
        return requestMapper.map(requests, true);
    }
}
