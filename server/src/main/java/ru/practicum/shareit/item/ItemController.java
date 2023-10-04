package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comments.model.dto.CommentDtoRequest;
import ru.practicum.shareit.item.comments.model.dto.CommentDtoResponse;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemDtoRequest;
import ru.practicum.shareit.item.model.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto create( @RequestBody ItemDtoRequest item,
                          @RequestHeader(USER_ID) Long ownerId) {
        log.info("Попытка создания новой вещи {}", item);
        return itemService.saveItem(item, ownerId);
    }

    @PatchMapping("{itemId}")
    public ItemDto update(@PathVariable("itemId") Long itemId,
                          @RequestBody ItemDtoRequest itemDtoRequest,
                          @RequestHeader(USER_ID) Long userId) {
        log.info("Попытка обновления item id = {}", itemId);
        itemDtoRequest.setId(itemId);
        return itemService.updateItem(itemDtoRequest, userId);
    }

    @GetMapping("{itemId}")
    public ItemDtoResponse findItemById(@PathVariable("itemId") Long itemId,
                                        @RequestHeader(USER_ID) Long userId) {
        return itemService.findItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoResponse> findUsersItems(@RequestHeader(USER_ID) Long ownerId,
                                                @RequestParam(name = "from") int from,
                                                @RequestParam(name = "size") int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return itemService.findUsersItems(ownerId, pageable);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text,
                                     @RequestHeader(USER_ID) Long renterId,
                                     @RequestParam(name = "from") int from,
                                     @RequestParam(name = "size") int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return itemService.searchItems(text, renterId, pageable);
    }

    @PostMapping("{itemId}/comment")
    public CommentDtoResponse createComment(@RequestBody CommentDtoRequest comment,
                                            @PathVariable("itemId") Long itemId,
                                            @RequestHeader(USER_ID) Long userId) {
        log.info("Попытка создания нового комментария {} to Item id = {}", comment, itemId);
        return itemService.saveComment(comment, itemId, userId);
    }
}
