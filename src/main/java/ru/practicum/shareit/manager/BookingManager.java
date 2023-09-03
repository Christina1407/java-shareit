package ru.practicum.shareit.manager;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.enums.EnumStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.exception.NotAllowedException;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class BookingManager {
    private final BookingRepository bookingRepository;
    private final List<EnumStatus> statuses = List.of(EnumStatus.CANCELED, EnumStatus.REJECTED);

    public Booking findNextBooking(Item item) {
        return bookingRepository.findTopByItem_IdAndStartDateGreaterThanEqualAndStatusNotInOrderByStartDate(
                item.getId(), LocalDateTime.now(), statuses);
    }

    public Booking findLastBooking(Item item) {
        return bookingRepository.findTopByItem_IdAndStartDateLessThanEqualAndStatusNotInOrderByStartDateDesc(
                item.getId(), LocalDateTime.now(), statuses);
    }

    public List<Booking> findBookingsByItemIdAndBookerId(Long itemId, Long bookerId) {
        List<Booking> bookings = bookingRepository.findByItem_IdAndStatusInAndBooker_IdAndEndDateLessThanEqual(
                itemId, List.of(EnumStatus.APPROVED), bookerId, LocalDateTime.now());
        if (bookings.isEmpty()) {
            log.error("Item id = {} doesn't have PAST bookings from user id = {}", itemId, bookerId);
            throw new NotAllowedException();
        }
        return bookings;
    }


}
