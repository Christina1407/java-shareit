package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.enums.EnumStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.exception.NotAllowedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comments.model.dto.CommentDtoRequest;
import ru.practicum.shareit.item.comments.model.dto.CommentDtoResponse;
import ru.practicum.shareit.item.comments.model.dto.CommentMapper;
import ru.practicum.shareit.item.comments.repo.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemDtoRequest;
import ru.practicum.shareit.item.model.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.dto.ItemMapper;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.manager.ItemManager;
import ru.practicum.shareit.manager.UserManager;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repo.RequestRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
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
    private final CommentMapper commentMapper;
    private final UserManager userManager;
    private final ItemManager itemManager;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final RequestRepository requestRepository;
    private final List<EnumStatus> closedStatuses = List.of(EnumStatus.CANCELED, EnumStatus.REJECTED);

    @Override
    public ItemDto saveItem(ItemDtoRequest itemDto, Long ownerId) {
        User user = userManager.findUserById(ownerId);
        Request request = null;
        if (Objects.nonNull(itemDto.getRequestId())) {
            request = requestRepository.findById(itemDto.getRequestId()).orElseThrow(NotFoundException::new);
        }
        Item item = itemMapper.map(itemDto, user, request);
        return itemMapper.map(itemRepository.save(item));
    }

    @Override
    public CommentDtoResponse saveComment(CommentDtoRequest commentDtoRequest, Long itemId, Long userId) {
        User user = userManager.findUserById(userId);
        Item item = itemManager.findItemById(itemId);
        //проверка, что у юзера есть завершившиеся бронирования этой вещи
        existsPastBookingsByItemIdAndBookerId(itemId, userId);
        return commentMapper.map(commentRepository.save(commentMapper.map(commentDtoRequest, user, item)));
    }

    @Override
    public ItemDto updateItem(ItemDtoRequest itemDto, Long ownerId) {
        //проверка, что айдишники существующих юзера и вещи
        User user = userManager.findUserById(ownerId);
        itemManager.findItemById(itemDto.getId());
        if (user.getItems().stream().noneMatch(item -> item.getId().equals(itemDto.getId()))) {
            log.error("Редактировать вещь может только её владелец. Пользователь c id = {} не владелец вещи {}", ownerId, itemDto);
            throw new NotFoundException();
        }
        Item item = itemMapper.map(itemDto, user, null);
        Item itemForUpdate = preUpdate(user, item);
        return itemMapper.map(itemRepository.save(itemForUpdate));
    }

    @Override
    public ItemDtoResponse findItemById(Long itemId, Long userId) {
        userManager.findUserById(userId);
        Item item = itemManager.findItemById(itemId);
        if (item.getOwner().getId().equals(userId)) {
            return itemMapper.map(item, findLastBooking(item), findNextBooking(item), commentMapper.map(item.getComments()));
        } else {
            return itemMapper.map(item, null, null, commentMapper.map(item.getComments()));
        }
    }

    @Override
    public List<ItemDtoResponse> findUsersItems(Long ownerId, Pageable pageable) {
        User user = userManager.findUserById(ownerId);
        return mapItemList(itemRepository.findByOwner_Id(user.getId(), pageable));
    }

    @Override
    public List<ItemDto> searchItems(String text, Long renterId, Pageable pageable) {
        userManager.findUserById(renterId);
        if (Objects.nonNull(text) && text.isBlank()) {
            return new ArrayList<>();
        }
        return itemMapper.map(itemRepository.searchItemsByNameAndDescription("%" + text + "%", pageable));
    }

    private List<ItemDtoResponse> mapItemList(List<Item> items) {
        List<ItemDtoResponse> responseList = new ArrayList<>();
        items.forEach(
                item -> {
                    responseList.add(itemMapper.map(item, findLastBooking(item), findNextBooking(item),
                            commentMapper.map(item.getComments())));
                }
        );
        return responseList;
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

    private Booking findNextBooking(Item item) {
        return bookingRepository.findTopByItem_IdAndStartDateGreaterThanEqualAndStatusNotInOrderByStartDate(
                item.getId(), LocalDateTime.now(), closedStatuses);
    }

    private Booking findLastBooking(Item item) {
        return bookingRepository.findTopByItem_IdAndStartDateLessThanEqualAndStatusNotInOrderByStartDateDesc(
                item.getId(), LocalDateTime.now(), closedStatuses);
    }

    private void existsPastBookingsByItemIdAndBookerId(Long itemId, Long bookerId) {
        boolean exists = bookingRepository.existsByItem_IdAndStatusInAndBooker_IdAndEndDateLessThanEqual(
                itemId, List.of(EnumStatus.APPROVED), bookerId, LocalDateTime.now());
        if (!exists) {
            log.error("Item id = {} doesn't have PAST bookings from user id = {}", itemId, bookerId);
            throw new NotAllowedException();
        }
    }
}
