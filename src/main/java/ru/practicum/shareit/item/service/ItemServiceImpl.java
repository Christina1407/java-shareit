package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemMapper;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.user.repo.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;

    @Override
    public ItemDto saveItem(ItemDto itemDto, Long ownerId) {
        userRepository.findUserById(ownerId);
        Item item = itemMapper.map(itemDto, ownerId);
        return itemMapper.map(itemRepository.saveItem(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long ownerId) {
        //проверка, что айдишники существующих юзера и вещи
        userRepository.findUserById(ownerId);
        itemRepository.findItemById(itemDto.getId());
        if (itemRepository.getItemsByUserId(ownerId).stream().noneMatch(item -> item.getOwnerId().equals(ownerId))) {
            log.error("Редактировать вещь может только её владелец. Пользователь c id = {} не владелец вещи {}", ownerId, itemDto);
            throw new NotFoundException();
        }
        Item item = itemMapper.map(itemDto, ownerId);
        return itemMapper.map(itemRepository.updateItem(item, ownerId));
    }

    @Override
    public ItemDto findItemById(Long itemId, Long ownerId) {
        userRepository.findUserById(ownerId);
        return itemMapper.map(itemRepository.findItemById(itemId));
    }

    @Override
    public List<ItemDto> findUsersItems(Long ownerId) {
        userRepository.findUserById(ownerId);
        return itemMapper.map(itemRepository.findUsersItems(ownerId));
    }

    @Override
    public List<ItemDto> searchItems(String text, Long renterId) {
        userRepository.findUserById(renterId);
        return itemMapper.map(itemRepository.searchItems(text));
    }
}
