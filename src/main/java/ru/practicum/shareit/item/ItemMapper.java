package ru.practicum.shareit.item;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {
    public List<ItemDto> map(List<Item> items) {
        return items.stream()
                .map(this::getBuild)
                .collect(Collectors.toList());
    }

    private ItemDto getBuild(Item item) {
        return ItemDto.builder()
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public ItemDto map(Item item) {
        return getBuild(item);
    }
}
