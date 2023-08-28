package ru.practicum.shareit.item.repo;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item saveItem(Item item);

    Item updateItem(Item item, Long ownerId);

    List<Item> getItemsByUserId(Long ownerId);

    Item findItemById(Long itemId);

    List<Item> findUsersItems(Long ownerId);

    List<Item> searchItems(String text);
}
