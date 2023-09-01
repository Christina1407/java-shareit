package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingMapper;
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
//                    item.setAvailable(false);
//                    itemRepository.save(item);
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
    public List<BookingDtoResponse> findUsersBookings(Long bookerId, EnumState state) {
        userManager.findUserById(bookerId);
        List<EnumStatus> statusList = List.of(EnumStatus.REJECTED, EnumStatus.WAITING, EnumStatus.APPROVED, EnumStatus.CANCELED);
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBooker_IdAndStatusInOrderByStartDateDesc(bookerId, statusList);
                break;
            case PAST:
            case CURRENT:
            case FUTURE:
                bookings = bookingRepository.findByBooker_IdAndStatusInAndStartDateGreaterThanEqualOrderByStartDateDesc(bookerId, List.of(EnumStatus.APPROVED, EnumStatus.WAITING),
                        LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findByBooker_IdAndStatusInOrderByStartDateDesc(bookerId, List.of(EnumStatus.WAITING));
                break;
            case REJECTED:
                bookings = bookingRepository.findByBooker_IdAndStatusInOrderByStartDateDesc(bookerId, List.of(EnumStatus.REJECTED, EnumStatus.CANCELED));
                break;
        }
        return bookingMapper.map(bookings);
    }
}
