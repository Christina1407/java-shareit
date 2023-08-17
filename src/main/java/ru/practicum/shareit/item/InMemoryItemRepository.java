package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.*;

@Repository
@Slf4j
public class InMemoryItemRepository implements ItemRepository{
    private final Map<Long, List<Item>> items = new HashMap<>();
    private long itemId;
    @Override
    public Item saveItem(Item item) {
        item.setId(getItemId());
        items.compute(item.getOwnerId(), (ownerId, userItems) -> {
            if(userItems == null) {
                userItems = new ArrayList<>();
            }
            userItems.add(item);
            return userItems;
        });

        return item;
    }

    @Override
    public Item updateItem(Item item, Long ownerId) {
        Item itemForUpdate = getItemsByUserId(ownerId).stream()
                .filter(i -> i.getId().equals(item.getId()))
                .findFirst()
                .orElseThrow();
        if (Objects.nonNull(item.getName()) && !item.getName().isBlank()) { // если имя пустое, то оно не изменится
            itemForUpdate.setName(item.getName());
        }
        Optional.ofNullable(item.getDescription()).ifPresent(description -> itemForUpdate.setDescription(item.getDescription())); //описание может быть пустым
        Optional.ofNullable(item.getAvailable()).ifPresent(available -> itemForUpdate.setAvailable(item.getAvailable()));
        return itemForUpdate;

    }

    @Override
    public List<Item> getItemsByUserId(Long ownerId) {
        return items.getOrDefault(ownerId, Collections.emptyList());
    }

    @Override
    public Item findItemById(Long itemId) {
        return items.values().stream()
                .flatMap(Collection::stream)
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Item c id = {} не найдена", itemId);
                    return new NotFoundException();
                });
    }

    @Override
    public List<Item> findUsersItems(Long ownerId) {
        return items.getOrDefault(ownerId, Collections.emptyList());
    }


    private long getItemId() {
        return ++itemId;
    }
}
