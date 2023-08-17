package ru.practicum.shareit.item;

import java.util.List;

public interface ItemRepository {
    Item saveItem(Item item);

    Item updateItem(Item item, Long ownerId);

    List<Item> getItemsByUserId(Long ownerId);

    Item findItemById(Long itemId);

    List<Item> findUsersItems(Long ownerId);
}
