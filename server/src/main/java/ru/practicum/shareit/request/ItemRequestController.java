package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.model.dto.RequestDto;
import ru.practicum.shareit.request.model.dto.RequestDtoResponse;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
@Slf4j
public class ItemRequestController {
    private final RequestService requestService;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public RequestDtoResponse create(@RequestBody RequestDto requestDto,
                                     @RequestHeader(USER_ID) Long requesterId) {
        log.info("Попытка создания нового запроса {}", requestDto);
        return requestService.saveRequest(requestDto, requesterId);
    }

    @GetMapping("{requestId}")
    public RequestDtoResponse findRequestById(@PathVariable("requestId") Long requestId,
                                              @RequestHeader(USER_ID) Long userId) {
        return requestService.findRequestById(requestId, userId);
    }

    @GetMapping
    public List<RequestDtoResponse> findUsersRequests(@RequestHeader(USER_ID) Long requesterId) {
        return requestService.findUsersRequests(requesterId);
    }

    @GetMapping("/all")
    public List<RequestDtoResponse> findRequests(@RequestHeader(USER_ID) Long userId,
                                                 @RequestParam(name = "from") int from,
                                                 @RequestParam(name = "size") int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return requestService.findRequests(userId, pageable);
    }

}
