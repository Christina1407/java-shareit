package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.enums.EnumState;
import ru.practicum.shareit.booking.enums.EnumStatus;
import ru.practicum.shareit.booking.model.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.exception.NotAllowedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.manager.ItemManager;
import ru.practicum.shareit.manager.UserManager;
import ru.practicum.shareit.user.model.User;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserManager userManager;
    private final ItemManager itemManager;
    private final ItemRepository itemRepository;

    @Override
    public BookingDtoResponse saveBooking(BookingDtoRequest bookingDtoRequest, Long ownerId) {
        if (!bookingDtoRequest.getEnd().isAfter(bookingDtoRequest.getStart())) {
            throw new ValidationException("End must be after start");
        }
        User user = userManager.findUserById(ownerId);
        Item item = itemManager.findItemById(bookingDtoRequest.getItemId());
        if (item.getAvailable()) {
            if (!item.getOwner().getId().equals(ownerId)) {
                Booking booking = bookingMapper.map(bookingDtoRequest, user, item, EnumStatus.WAITING);
                return bookingMapper.map(bookingRepository.save(booking));
            } else {
                log.error("Вещь id = {} не может быть забронирована владельцем вещи  userId = {}", item.getId(), ownerId);
                throw new NotFoundException();
            }
        } else {
            log.error("Вещь id = {} недоступна для бронирования available is {}", item.getId(), item.getAvailable());
            throw new NotAllowedException();
        }
    }

    @Override
    public BookingDtoResponse approveOrRejectBooking(Long bookingId, Long userId, Boolean approved) {
        userManager.findUserById(userId);
        Booking booking = getBooking(bookingId, userId);
        Item item = booking.getItem();
        if (booking.getStatus().equals(EnumStatus.WAITING)) {
            if (booking.getBooker().getId().equals(userId)) {
                if (approved) {
                    log.error("Пользователь c id = {} хочет подтвердить бронирование id = {}. " +
                            "Подтвердить бронирование может только владелец вещи, к которой относится бронирование", userId, bookingId);
                    throw new NotFoundException();
                } else {
                    booking.setStatus(EnumStatus.CANCELED);
                    bookingRepository.save(booking);
                    return bookingMapper.map(booking);
                }
            } else {
                if (approved) {
                    booking.setStatus(EnumStatus.APPROVED);
                    bookingRepository.save(booking);
                } else {
                    booking.setStatus(EnumStatus.REJECTED);
                    bookingRepository.save(booking);
                }
                return bookingMapper.map(booking);
            }
        } else {
            log.error("Booking id = {} уже подтверждено или отклонено ", bookingId);
            throw new NotAllowedException();
        }
    }

    @Override
    public BookingDtoResponse findBookingById(Long bookingId, Long userId) {
        Booking booking = getBooking(bookingId, userId);
        return bookingMapper.map(booking);
    }

    private Booking getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(NotFoundException::new);
        userManager.findUserById(userId);
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            return booking;
        } else {
            log.error("Пользователь c id = {} хочет получить данные бронирования id = {}. " +
                    "Данные о бронировании доступны либо автору бронирования, либо владельцу вещи, к которой относится бронирование.", userId, bookingId);
            throw new NotFoundException();
        }
    }

    @Override
    public List<BookingDtoResponse> findUsersBookings(Long userId, EnumState state) {
        userManager.findUserById(userId);
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBooker_IdOrderByStartDateDesc(userId);
                break;
            case PAST:
                bookings = bookingRepository.findByBooker_IdAndEndDateLessThanEqualOrderByStartDateDesc(userId, LocalDateTime.now());
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentBookings(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findByBooker_IdAndStartDateGreaterThanEqualOrderByStartDateDesc(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findByBooker_IdAndStatusInOrderByStartDateDesc(userId, List.of(EnumStatus.WAITING));
                break;
            case REJECTED:
                bookings = bookingRepository.findByBooker_IdAndStatusInOrderByStartDateDesc(userId, List.of(EnumStatus.REJECTED, EnumStatus.CANCELED));
                break;
        }
        return bookingMapper.map(bookings);
    }

    @Override
    public List<BookingDtoResponse> findOwnersBookings(Long ownerId, EnumState state) {
        User user =  userManager.findUserById(ownerId);
        List<Item> items = user.getItems();
        if (items.isEmpty()) {
            log.error("У пользователя c id = {} нет вещей {}", ownerId, items);
            throw new NotFoundException();
        }
        List<Long> itemsIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Booking> bookings = new ArrayList<>();

        switch (state) {
            case ALL:
                bookings = bookingRepository.findByItem_IdInOrderByStartDateDesc(itemsIds);
                break;
            case PAST:
                bookings = bookingRepository.findByItem_IdInAndEndDateLessThanEqualOrderByStartDateDesc(itemsIds, LocalDateTime.now());
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentOwnerBookings(itemsIds, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findByItem_IdInAndStartDateGreaterThanEqualOrderByStartDateDesc(itemsIds, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findByItem_IdInAndStatusInOrderByStartDateDesc(itemsIds, List.of(EnumStatus.WAITING));
                break;
            case REJECTED:
                bookings = bookingRepository.findByItem_IdInAndStatusInOrderByStartDateDesc(itemsIds, List.of(EnumStatus.REJECTED, EnumStatus.CANCELED));
                break;
        }
        return bookingMapper.map(bookings);
    }
}
