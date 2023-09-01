package ru.practicum.shareit.manager;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepository;

@Component
@AllArgsConstructor
public class ItemManager {
    private final ItemRepository itemRepository;
    public Item findItemById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(NotFoundException::new);
    }
}
