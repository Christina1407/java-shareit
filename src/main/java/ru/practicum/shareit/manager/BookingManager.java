package ru.practicum.shareit.manager;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.booking.enums.EnumStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
public class BookingManager {
    private final BookingRepository bookingRepository;
    private final List<EnumStatus> statuses = List.of(EnumStatus.CANCELED, EnumStatus.REJECTED);
    public Booking findNextBooking(Item item) {
        return bookingRepository.findTopByItem_IdAndStartDateGreaterThanEqualAndStatusNotInOrderByStartDate(
                item.getId(), LocalDateTime.now(),statuses);
    }

    public Booking findLastBooking(Item item) {
        return bookingRepository.findTopByItem_IdAndEndDateLessThanEqualAndStatusNotInOrderByStartDateDesc(
                item.getId(), LocalDateTime.now(), statuses);
    }
}
