package ru.practicum.shareit.booking.model.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.enums.EnumStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemBookingDto;
import ru.practicum.shareit.user.UserBookingDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookingMapper {
    public List<BookingDtoResponse> map(List<Booking> bookings) {
        return bookings.stream()
                .map(this::getBuild)
                .collect(Collectors.toList());
    }

    private BookingDtoResponse getBuild(Booking booking) {
        return BookingDtoResponse.builder()
                .id(booking.getId())
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .status(booking.getStatus())
                .booker(UserBookingDto.builder()
                        .id(booking.getBooker().getId())
                        .build())
                .item(ItemBookingDto.builder()
                        .id(booking.getItem().getId())
                        .name(booking.getItem().getName())
                        .build())
                .build();
    }

    private Booking getBuild(BookingDtoRequest bookingDto, User user, Item item, EnumStatus status) {
        return Booking.builder()
                .startDate(bookingDto.getStart())
                .endDate(bookingDto.getEnd())
                .item(item)
                .booker(user)
                .status(status)
                .build();
    }

    public BookingDtoResponse map(Booking booking) {
        return getBuild(booking);
    }

    public Booking map(BookingDtoRequest bookingDto, User user, Item item, EnumStatus status) {
        return getBuild(bookingDto, user, item, status);
    }
}
