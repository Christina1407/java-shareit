package ru.practicum.shareit.item;

import ru.practicum.shareit.user.model.dto.UserDto;

import java.util.List;

public interface ItemService {
    ItemDto saveItem(ItemDto itemDto, Long ownerId);

    ItemDto updateItem(ItemDto itemDto, Long ownerId);

    ItemDto findItemById(Long itemId, Long ownerId);

    List<ItemDto> findUsersItems(Long ownerId);

    List<ItemDto> searchItems(String text);
}

