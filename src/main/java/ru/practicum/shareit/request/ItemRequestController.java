package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.OnCreate;
import ru.practicum.shareit.request.model.dto.RequestDto;
import ru.practicum.shareit.request.model.dto.RequestDtoResponse;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final RequestService requestService;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public RequestDtoResponse create(@Validated(OnCreate.class) @RequestBody RequestDto requestDto,
                                     @RequestHeader(USER_ID) @Min(1) Long requesterId) {
        log.info("Попытка создания нового запроса {}", requestDto);
        return requestService.saveRequest(requestDto, requesterId);
    }

    @GetMapping("{requestId}")
    public RequestDtoResponse findRequestById(@PathVariable("requestId") @Min(1) Long requestId,
                                        @RequestHeader(USER_ID) @NotNull @Min(1) Long userId) {
        return requestService.findRequestById(requestId, userId);
    }

    @GetMapping
    public List<RequestDtoResponse> findUsersRequests(@RequestHeader(USER_ID) @NotNull @Min(1) Long requesterId) {
        return requestService.findUsersRequests(requesterId);
    }
    @GetMapping("/all")
    public List<RequestDtoResponse> findRequests(@RequestHeader(USER_ID) @NotNull @Min(1) Long userId,
                                                 @RequestParam(name = "from", defaultValue = "0")  @Min(0) int from,
                                                 @RequestParam(name = "size", defaultValue = "10") @Min(1) int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return requestService.findRequests(userId, pageable);
    }

}
