package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.OnCreate;
import ru.practicum.shareit.booking.enums.EnumState;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoResponse;

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
                                                 @RequestParam(name = "from", defaultValue = "0") Long from,
                                                 @RequestParam(name = "size", defaultValue = "10") Long size) {
        Pageable pageable = PageRequest.of(Math.toIntExact(from / size), Math.toIntExact(size));
        return requestService.findUsersRequests(userId);
    }

}
