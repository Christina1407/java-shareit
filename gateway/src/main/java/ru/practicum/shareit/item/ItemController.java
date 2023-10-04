package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.OnCreate;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoRequest;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@Validated(OnCreate.class) @RequestBody ItemDtoRequest item,
                          @RequestHeader(USER_ID) @Min(1) Long ownerId) {
        log.info("Попытка создания новой вещи {}", item);
        return itemClient.saveItem(item, ownerId);
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<Object> update(@PathVariable("itemId") @Min(1) Long itemId,
                          @RequestBody ItemDtoRequest itemDtoRequest,
                          @RequestHeader(USER_ID) @NotNull @Min(1) Long userId) {
        log.info("Попытка обновления item id = {}", itemId);
        return itemClient.updateItem(itemDtoRequest, userId, itemId);
    }

    @GetMapping("{itemId}")
    public ResponseEntity<Object> findItemById(@PathVariable("itemId") @Min(1) Long itemId,
                                               @RequestHeader(USER_ID) @NotNull @Min(1) Long userId) {
        return itemClient.findItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findUsersItems(@RequestHeader(USER_ID) @NotNull @Min(1) Long ownerId,
                                                       @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                                       @RequestParam(name = "size", defaultValue = "10") @Min(1) int size) {
        return itemClient.findUsersItems(ownerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam @NotNull String text,
                                     @RequestHeader(USER_ID) @NotNull @Min(1) Long renterId,
                                     @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                     @RequestParam(name = "size", defaultValue = "10") @Min(1) int size) {
        return itemClient.searchItems(renterId, text, from, size);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDtoRequest comment,
                                            @PathVariable("itemId") @Min(1) Long itemId,
                                            @RequestHeader(USER_ID) @Min(1) Long userId) {
        log.info("Попытка создания нового комментария {} to Item id = {}", comment, itemId);
        return itemClient.saveComment(userId, itemId, comment);
    }
}
