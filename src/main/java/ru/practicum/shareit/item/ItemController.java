package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.OnCreate;
import ru.practicum.shareit.item.comments.model.dto.CommentDtoRequest;
import ru.practicum.shareit.item.comments.model.dto.CommentDtoResponse;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemDtoRequest;
import ru.practicum.shareit.item.model.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemService itemService;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping()
    public ItemDto create(@Validated(OnCreate.class) @RequestBody ItemDtoRequest item,
                          @RequestHeader(USER_ID) @Min(1) Long ownerId) {
        log.info("Попытка создания новой вещи {}", item);
        return itemService.saveItem(item, ownerId);
    }

    @PatchMapping("{itemId}")
    public ItemDto update(@PathVariable("itemId") @Min(1) Long itemId,
                          @RequestBody ItemDtoRequest itemDto,
                          @RequestHeader(USER_ID) @NotNull @Min(1) Long ownerId) {
        log.info("Попытка обновления item id = {}", itemId);
        itemDto.setId(itemId);
        return itemService.updateItem(itemDto, ownerId);
    }

    @GetMapping("{itemId}")
    public ItemDtoResponse findItemById(@PathVariable("itemId") @Min(1) Long itemId,
                                        @RequestHeader(USER_ID) @NotNull @Min(1) Long userId) {
        return itemService.findItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoResponse> findUsersItems(@RequestHeader(USER_ID) @NotNull @Min(1) Long ownerId) {
        return itemService.findUsersItems(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam @NotNull String text,
                                     @RequestHeader(USER_ID) @NotNull @Min(1) Long renterId) {
        return itemService.searchItems(text, renterId);
    }

    @PostMapping("{itemId}/comment")
    public CommentDtoResponse createComment(@Valid @RequestBody CommentDtoRequest comment,
                                            @PathVariable("itemId") @Min(1) Long itemId,
                                            @RequestHeader(USER_ID) @Min(1) Long userId) {
        log.info("Попытка создания нового комментария {} to Item id = {}", comment, itemId);
        return itemService.saveComment(comment, itemId, userId);
    }
}
