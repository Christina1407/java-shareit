package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.OnCreate;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final RequestClient requestClient;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@Validated(OnCreate.class) @RequestBody RequestDto requestDto,
                                         @RequestHeader(USER_ID) @Min(1) Long requesterId) {
        log.info("Попытка создания нового запроса {}", requestDto);
        return requestClient.saveRequest(requestDto, requesterId);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> findRequestById(@PathVariable("requestId") @Min(1) Long requestId,
                                                  @RequestHeader(USER_ID) @NotNull @Min(1) Long userId) {
        return requestClient.findRequestById(requestId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findUsersRequests(@RequestHeader(USER_ID) @NotNull @Min(1) Long requesterId) {
        return requestClient.findUsersRequests(requesterId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findRequests(@RequestHeader(USER_ID) @NotNull @Min(1) Long userId,
                                               @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                               @RequestParam(name = "size", defaultValue = "10") @Min(1) int size) {
        return requestClient.findRequests(userId, from, size);
    }

}
