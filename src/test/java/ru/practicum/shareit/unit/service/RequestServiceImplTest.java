package ru.practicum.shareit.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.manager.UserManager;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.model.dto.RequestDto;
import ru.practicum.shareit.request.model.dto.RequestDtoResponse;
import ru.practicum.shareit.request.model.dto.RequestMapper;
import ru.practicum.shareit.request.repo.RequestRepository;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    private RequestService requestService;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private UserManager userManager;
    @Mock
    private RequestMapper requestMapper;

    @BeforeEach
    void setUp() {
        requestService = new RequestServiceImpl(requestRepository, userManager, requestMapper);
    }


    @Test
    void saveRequest() {
        //before
        RequestDto requestDto = new RequestDto();
        Long requesterId = 1L;
        User user = new User();
        Request request = new Request();
        Request savedRequest = new Request();
        RequestDtoResponse requestDtoResponse = new RequestDtoResponse(null, null, null, null);
        when(userManager.findUserById(requesterId)).thenReturn(user);
        when(requestMapper.map(requestDto, user)).thenReturn(request);
        when(requestRepository.save(request)).thenReturn(savedRequest);
        when(requestMapper.map(savedRequest, false)).thenReturn(requestDtoResponse);
        //when
        RequestDtoResponse finalRequestDtoResponse = requestService.saveRequest(requestDto, requesterId);
        //then
        assertThat(finalRequestDtoResponse).isEqualTo(requestDtoResponse);
        verify(userManager, only()).findUserById(requesterId);
        verify(requestMapper, times(1)).map(requestDto, user);
        verify(requestMapper, times(1)).map(savedRequest, false);
    }

    @Test
    void findRequestById() {
        //before
        Long requestId = 1L;
        Long userId = 1L;
        Request request = new Request();
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));

        RequestDtoResponse requestDtoResponse = new RequestDtoResponse(null, null, null, null);
        when(requestMapper.map(request, true)).thenReturn(requestDtoResponse);
        //when
        RequestDtoResponse finalRequestDtoResponse = requestService.findRequestById(requestId, userId);
        //then
        verify(userManager, only()).findUserById(userId);
        verify(requestRepository, only()).findById(requestId);
        verify(requestMapper, only()).map(request, true);

        assertThat(finalRequestDtoResponse).isEqualTo(requestDtoResponse);

        //before
        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());
        //when
        assertThatThrownBy(() -> requestService.findRequestById(requestId, userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(null);
    }
}