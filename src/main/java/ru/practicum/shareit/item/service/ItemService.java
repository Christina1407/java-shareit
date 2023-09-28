package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.comments.model.dto.CommentDtoRequest;
import ru.practicum.shareit.item.comments.model.dto.CommentDtoResponse;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemDtoRequest;
import ru.practicum.shareit.item.model.dto.ItemDtoResponse;

import java.util.List;

public interface ItemService {
    ItemDto saveItem(ItemDtoRequest itemDto, Long ownerId);

    ItemDto updateItem(ItemDtoRequest itemDto, Long userId);

    ItemDtoResponse findItemById(Long itemId, Long userId);

    List<ItemDtoResponse> findUsersItems(Long ownerIdle, Pageable pageable);

    List<ItemDto> searchItems(String text, Long renterId, Pageable pageable);

    CommentDtoResponse saveComment(CommentDtoRequest comment, Long itemId, Long userId);
}

