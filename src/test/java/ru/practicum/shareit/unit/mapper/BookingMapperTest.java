package ru.practicum.shareit.unit.mapper;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.enums.EnumStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.dto.BookingMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BookingMapperTest {
    private BookingMapper bookingMapper;

    @BeforeEach
    void setUp() {
        bookingMapper = new BookingMapper();
    }

    @Test
    void shouldBeSuccessfullyMapBooking() {
        //before
        Booking booking = Instancio.create(Booking.class);
        BookingDtoResponse mapped = bookingMapper.map(booking);
        assertThat(mapped.getId()).isEqualTo(booking.getId());
        assertThat(mapped.getStart()).isEqualTo(booking.getStartDate());
        assertThat(mapped.getEnd()).isEqualTo(booking.getEndDate());
        assertThat(mapped.getItem().getId()).isEqualTo(booking.getItem().getId());
        assertThat(mapped.getItem().getName()).isEqualTo(booking.getItem().getName());
        assertThat(mapped.getBooker().getId()).isEqualTo(booking.getBooker().getId());
        assertThat(mapped.getBooker().getId()).isEqualTo(booking.getBooker().getId());
        assertThat(mapped.getStatus()).isEqualTo(booking.getStatus());
    }

    @Test
    void shouldBeSuccessfullyMapBookingDtoRequest() {
        //before
        BookingDtoRequest bookingDtoRequest = Instancio.create(BookingDtoRequest.class);
        User user = Instancio.create(User.class);
        Item item = Instancio.create(Item.class);
        EnumStatus enumStatus = Instancio.create(EnumStatus.class);
        //when
        Booking mapped = bookingMapper.map(bookingDtoRequest, user, item, enumStatus);
        //then
        assertThat(mapped.getId()).isNull();
        assertThat(mapped.getStartDate()).isEqualTo(bookingDtoRequest.getStart());
        assertThat(mapped.getEndDate()).isEqualTo(bookingDtoRequest.getEnd());
        assertThat(mapped.getItem()).isEqualTo(item);
        assertThat(mapped.getBooker()).isEqualTo(user);
        assertThat(mapped.getStatus()).isEqualTo(enumStatus);
    }

}