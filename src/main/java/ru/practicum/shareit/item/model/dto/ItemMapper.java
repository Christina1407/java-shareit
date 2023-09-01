package ru.practicum.shareit.item.model.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapper {
    public List<ItemDto> map(List<Item> items) {
        return items.stream()
                .map(this::getBuild)
                .collect(Collectors.toList());
    }

    private ItemDto getBuild(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    private Item getBuild(ItemDto item, User user) {
        return Item.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(user)
                .build();
    }

    public ItemDto map(Item item) {
        return getBuild(item);
    }

    public Item map(ItemDto item, User user) {
        return getBuild(item, user);
    }
}
