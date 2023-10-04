package ru.practicum.shareit.item.model.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.BookingItemDto;
import ru.practicum.shareit.item.comments.model.dto.CommentDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Component
public class ItemMapper {
    public List<ItemDto> map(List<Item> items) {
        return items.stream()
                .map(this::getBuild)
                .collect(Collectors.toList());
    }

    private ItemDtoResponse getBuild(Item item, Booking lastBooking, Booking nextBooking, List<CommentDtoResponse> comments) {
        ItemDtoResponse itemDtoResponse = ItemDtoResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(comments)
                .build();
        if (nonNull(lastBooking)) {
            itemDtoResponse.setLastBooking(BookingItemDto.builder()
                    .id(lastBooking.getId())
                    .bookerId(lastBooking.getBooker().getId())
                    .build());
        }
        if (nonNull(nextBooking)) {
            itemDtoResponse.setNextBooking(BookingItemDto.builder()
                    .id(nextBooking.getId())
                    .bookerId(nextBooking.getBooker().getId())
                    .build());
        }
        return itemDtoResponse;
    }

    private ItemDto getBuild(Item item) {
        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
        if (Objects.nonNull(item.getRequest())) {
            itemDto.setRequestId(item.getRequest().getId());
        }
        return itemDto;
    }

    private Item getBuild(ItemDtoRequest item, User user, Request request) {
        return Item.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(user)
                .request(request)
                .build();
    }

    public ItemDtoResponse map(Item item, Booking lastBooking, Booking nextBooking, List<CommentDtoResponse> comments) {
        return getBuild(item, lastBooking, nextBooking, comments);
    }

    public ItemDto map(Item item) {
        return getBuild(item);
    }

    public Item map(ItemDtoRequest item, User user, Request request) {
        return getBuild(item, user, request);
    }
}
