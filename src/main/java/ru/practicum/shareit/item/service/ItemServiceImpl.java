package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemMapper;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;

    @Override
    public ItemDto saveItem(ItemDto itemDto, Long ownerId) {
        User user = checkUser(ownerId);
        Item item = itemMapper.map(itemDto, user);
        return itemMapper.map(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long ownerId) {
        //проверка, что айдишники существующих юзера и вещи
        User user = checkUser(ownerId);
        itemRepository.findById(itemDto.getId()).orElseThrow(NotFoundException::new);
        if (user.getItems().stream().noneMatch(item -> item.getId().equals(itemDto.getId()))) {
            log.error("Редактировать вещь может только её владелец. Пользователь c id = {} не владелец вещи {}", ownerId, itemDto);
            throw new NotFoundException();
        }
        Item item = itemMapper.map(itemDto, user);
        Item itemForUpdate = preUpdate(user, item);
        return itemMapper.map(itemRepository.save(itemForUpdate));
    }


    @Override
    public ItemDto findItemById(Long itemId, Long ownerId) {
        checkUser(ownerId);
        Optional<Item> item = itemRepository.findById(itemId);
        return item.map(itemMapper::map).orElseThrow(NotFoundException::new);
    }

    @Override
    public List<ItemDto> findUsersItems(Long ownerId) {
        User user = checkUser(ownerId);
        return itemMapper.map(user.getItems());
    }

    @Override
    public List<ItemDto> searchItems(String text, Long renterId) {
        checkUser(renterId);
        if (Objects.nonNull(text) && text.isBlank()) {
            return new ArrayList<>();
        }
        return itemMapper.map(itemRepository.searchItemsByNameAndDescription("%" + text + "%"));
    }

    private Item preUpdate(User user, Item item) {
        Item itemForUpdate = user.getItems().stream()
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

    private User checkUser(Long ownerId) {
        return userRepository.findById(ownerId).orElseThrow(NotFoundException::new);
    }
}
