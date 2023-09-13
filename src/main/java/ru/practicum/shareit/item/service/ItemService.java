package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comments.model.dto.CommentDtoRequest;
import ru.practicum.shareit.item.comments.model.dto.CommentDtoResponse;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemDtoRequest;
import ru.practicum.shareit.item.model.dto.ItemDtoResponse;

import java.util.List;

public interface ItemService {
    ItemDto saveItem(ItemDtoRequest itemDto, Long ownerId);

    ItemDto updateItem(ItemDtoRequest itemDto, Long ownerId);

    ItemDtoResponse findItemById(Long itemId, Long userId);

    List<ItemDtoResponse> findUsersItems(Long ownerId);

    List<ItemDto> searchItems(String text, Long renterId);

    CommentDtoResponse saveComment(CommentDtoRequest comment, Long itemId, Long userId);
}

