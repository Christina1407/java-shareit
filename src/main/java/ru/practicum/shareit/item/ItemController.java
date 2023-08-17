package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.OnCreate;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Slf4j
@Validated
public class ItemController {
    ItemService itemService;

    @PostMapping()
    public ItemDto create(@Validated(OnCreate.class) @RequestBody ItemDto item,
                          @RequestHeader("X-Sharer-User-Id") @Min(1) Long ownerId) {
        log.info("Попытка создания новой вещи {}", item);
        return itemService.saveItem(item, ownerId);
    }

    @PatchMapping("{itemId}")
    public ItemDto update(@PathVariable("itemId") @Min(1) Long itemId,
                          @RequestBody ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") @NotNull @Min(1) Long ownerId) {
        log.info("Попытка обновления item id = {}", itemId);
        itemDto.setId(itemId);
        return itemService.updateItem(itemDto, ownerId);
    }

    @GetMapping("{itemId}")
    public ItemDto findItemById(@PathVariable("itemId") @Min(1) Long itemId,
                                @RequestHeader("X-Sharer-User-Id") @NotNull @Min(1) Long ownerId) {
        return itemService.findItemById(itemId, ownerId);
    }

    @GetMapping
    public List<ItemDto> findUsersItems(@RequestHeader("X-Sharer-User-Id") @NotNull @Min(1) Long ownerId) {
        return itemService.findUsersItems(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }

}
