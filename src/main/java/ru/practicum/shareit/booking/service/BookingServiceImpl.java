package ru.practicum.shareit.booking.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.enums.EnumState;
import ru.practicum.shareit.booking.enums.EnumStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.QBooking;
import ru.practicum.shareit.booking.model.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.dto.BookingMapper;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.exception.NotAllowedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.manager.ItemManager;
import ru.practicum.shareit.manager.UserManager;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserManager userManager;
    private final ItemManager itemManager;

    @Override
    public BookingDtoResponse saveBooking(BookingDtoRequest bookingDtoRequest, Long userId) {
        User user = userManager.findUserById(userId);
        Item item = itemManager.findItemById(bookingDtoRequest.getItemId());
        if (item.getAvailable()) {
            if (!item.getOwner().getId().equals(userId)) {
                //проверяем пересечение по времени с другими подтверждёнными бронированиями на эту вещь
                BooleanExpression intersectionDate = isIntersection(bookingDtoRequest.getStart(), bookingDtoRequest.getEnd(),
                        item, EnumStatus.APPROVED, null);
                boolean exists = bookingRepository.exists(intersectionDate);
                if (!exists) {
                    Booking booking = bookingMapper.map(bookingDtoRequest, user, item, EnumStatus.WAITING);
                    return bookingMapper.map(bookingRepository.save(booking));
                } else {
                    log.error("Вещь id = {} недоступна для бронирования в это время start = {}, end {}", item.getId(),
                            bookingDtoRequest.getStart(), bookingDtoRequest.getEnd());
                    throw new NotAllowedException();
                }
            } else {
                log.error("Вещь id = {} не может быть забронирована владельцем вещи  userId = {}", item.getId(), userId);
                throw new NotFoundException();
            }
        } else {
            log.error("Вещь id = {} недоступна для бронирования available is {}", item.getId(), item.getAvailable());
            throw new NotAllowedException();
        }
    }

    private static BooleanExpression isIntersection(LocalDateTime start, LocalDateTime end,
                                                    Item item, EnumStatus enumStatus, Long bookingId) {
        BooleanExpression intersection = QBooking.booking.item.id.eq(item.getId())
                .and(QBooking.booking.status.eq(enumStatus))
                .and(QBooking.booking.startDate.between(start, end)
                        .or(QBooking.booking.endDate.between(start, end))
                        .or(QBooking.booking.startDate.eq(start))
                        .or(QBooking.booking.endDate.eq(end))
                        .or(QBooking.booking.startDate.lt(start)
                                .and(QBooking.booking.endDate.gt(end))));
        if (nonNull(bookingId)) {
            intersection.and(QBooking.booking.id.ne(bookingId));
        }
        return intersection;
    }

    @Override
    public BookingDtoResponse approveOrRejectBooking(Long bookingId, Long userId, boolean approved) {
        Booking booking = getBooking(bookingId, userId);// если юзер не owner или booker, выдается ошибка

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
                    //находим бронирования для данной вещи со статусом WAITING, которые пересекаются по времени, и отменяем их
                    BooleanExpression intersectionDate = isIntersection(booking.getStartDate(), booking.getEndDate(), booking.getItem(),
                            EnumStatus.WAITING, bookingId);
                    Iterable<Booking> bookings = bookingRepository.findAll(intersectionDate);
                    bookings.forEach(b -> b.setStatus(EnumStatus.REJECTED));
                    booking.setStatus(EnumStatus.APPROVED);
                    bookingRepository.save(booking);
                } else {
                    booking.setStatus(EnumStatus.REJECTED);
                    bookingRepository.save(booking);
                }
                return bookingMapper.map(booking);
            }
        } else {
            log.error("Booking id = {} уже подтверждено или отклонено", bookingId);
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
    public List<BookingDtoResponse> findUsersBookings(Long userId, EnumState state, Pageable pageable) {
        userManager.findUserById(userId);
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBooker_IdOrderByStartDateDesc(userId, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findByBooker_IdAndEndDateLessThanEqualOrderByStartDateDesc(userId, LocalDateTime.now(), pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentBookings(userId, LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBooker_IdAndStartDateGreaterThanEqualOrderByStartDateDesc(userId, LocalDateTime.now(), pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findByBooker_IdAndStatusInOrderByStartDateDesc(userId, List.of(EnumStatus.WAITING), pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBooker_IdAndStatusInOrderByStartDateDesc(userId, List.of(EnumStatus.REJECTED, EnumStatus.CANCELED), pageable);
                break;
        }
        return bookingMapper.map(bookings);
    }

    @Override
    public List<BookingDtoResponse> findOwnersBookings(Long ownerId, EnumState state, Pageable pageable) {
        User user = userManager.findUserById(ownerId);
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
                bookings = bookingRepository.findByItem_IdInOrderByStartDateDesc(itemsIds, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findByItem_IdInAndEndDateLessThanEqualOrderByStartDateDesc(itemsIds, LocalDateTime.now(), pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentOwnerBookings(itemsIds, LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findByItem_IdInAndStartDateGreaterThanEqualOrderByStartDateDesc(itemsIds, LocalDateTime.now(), pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findByItem_IdInAndStatusInOrderByStartDateDesc(itemsIds, List.of(EnumStatus.WAITING), pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItem_IdInAndStatusInOrderByStartDateDesc(itemsIds, List.of(EnumStatus.REJECTED, EnumStatus.CANCELED), pageable);
                break;
        }
        return bookingMapper.map(bookings);
    }
}
