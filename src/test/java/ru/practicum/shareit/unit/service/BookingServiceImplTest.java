package ru.practicum.shareit.unit.service;

import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.crossstore.ChangeSetPersister;
import ru.practicum.shareit.booking.enums.EnumStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.dto.BookingMapper;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotAllowedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.manager.ItemManager;
import ru.practicum.shareit.manager.UserManager;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    private BookingService bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private UserManager userManager;
    @Mock
    private ItemManager itemManager;
    @Captor
    ArgumentCaptor<Predicate> intersectionDate;

    @Captor
    ArgumentCaptor<Booking> bookingArgumentCaptor;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(bookingRepository, bookingMapper, userManager, itemManager);
    }

    @Test
    void saveBooking() {
        //before
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(start, end, 5L, 10L);
        Long userId = 1L;
        User user = new User();

        when(userManager.findUserById(userId)).thenReturn(user);
        Item item = new Item();
        item.setId(10L);
        item.setAvailable(true);
        User itemOwner = new User();
        itemOwner.setId(2L);
        item.setOwner(itemOwner);
        when(itemManager.findItemById(bookingDtoRequest.getItemId())).thenReturn(item);

        when(bookingRepository.exists((Predicate) any())).thenReturn(false);

        Booking booking = new Booking();
        when(bookingMapper.map(bookingDtoRequest, user, item, EnumStatus.WAITING)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        BookingDtoResponse bookingDtoResponse = new BookingDtoResponse(null, null, null, null, null, null);
        when(bookingMapper.map(booking)).thenReturn(bookingDtoResponse);
        //when
        BookingDtoResponse result = bookingService.saveBooking(bookingDtoRequest, userId);
        // Пересечения по времени нет и available = true
        //then
        verify(bookingRepository, times(1)).exists(intersectionDate.capture());
        Predicate predicate = intersectionDate.getValue();
        // Проверяем, что корректно ищем пересечение по времени
        assertThat("booking.item.id = 10 && booking.status = APPROVED && (booking.startDate between " +
                start + " and " + end + " || booking.endDate between " +
                start + " and " + end + " || booking.startDate = " +
                start + " || booking.endDate = " + end + " || booking.startDate < " +
                start + " && booking.endDate > " + end + ")").isEqualTo(predicate.toString());
        verify(bookingMapper, times(1)).map(bookingDtoRequest, user, item, EnumStatus.WAITING);
        verify(bookingRepository, times(1)).save(booking);
        verify(bookingMapper, times(1)).map(booking);
        assertThat(bookingDtoResponse).isEqualTo(result);

        // Существует пересечение по времени и available = true
        //before
        when(bookingRepository.exists((Predicate) any())).thenReturn(true);
        //then
        assertThatThrownBy(() -> bookingService.saveBooking(bookingDtoRequest, userId))
                .isInstanceOf(NotAllowedException.class)
                .hasMessage(null);

        // available = true и владелец бронирует свою вещь
        //before
        itemOwner.setId(1L);
        //then
        assertThatThrownBy(() -> bookingService.saveBooking(bookingDtoRequest, userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(null);

        // available = false
        //before
        item.setAvailable(false);
        //then
        assertThatThrownBy(() -> bookingService.saveBooking(bookingDtoRequest, userId))
                .isInstanceOf(NotAllowedException.class)
                .hasMessage(null);
    }

    @Test
    void approveOrRejectBookingByItemOwner() {
        // FIXME доделать
//        //before
//        Long bookingId = 1L;
//        Long userId = 2L;
//        boolean approved = true;
//
//        Booking booking = new Booking();
//        User booker = new User();
//        booker.setId(2L);
//        booking.setBooker(booker);
//        booking.setStatus(EnumStatus.WAITING);
//
//        Item item = new Item();
//        User itemOwner = new User();
//        itemOwner.setId(1L);
//        item.setOwner(itemOwner);
//        booking.setItem(item);
//        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
//        //when
//        bookingService.approveOrRejectBooking(bookingId, userId, approved);
//        //then
//        verify(userManager, only()).findUserById(userId);
    }

    @Test
    void approveOrRejectBookingByBooker() {
        //before
        Long bookingId = 1L;
        Long userId = 2L;

        Booking booking = new Booking();
        User booker = new User();
        booker.setId(2L);
        booking.setBooker(booker);
        booking.setStatus(EnumStatus.WAITING);

        Item item = new Item();
        User itemOwner = new User();
        itemOwner.setId(1L);
        item.setOwner(itemOwner);
        booking.setItem(item);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        //when
        assertThatThrownBy(() -> bookingService.approveOrRejectBooking(bookingId, userId, true))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(null);
        //then
        verify(userManager, only()).findUserById(userId);

        //before
        BookingDtoResponse bookingDtoResponse = new BookingDtoResponse(null, null, null, null, null,null);
        when(bookingMapper.map(booking)).thenReturn(bookingDtoResponse);
        //when
        BookingDtoResponse result = bookingService.approveOrRejectBooking(bookingId, userId, false);
        //then
        verify(bookingRepository, times(1)).save(booking);
        verify(bookingMapper, only()).map(bookingArgumentCaptor.capture());
        Booking finalBooking = bookingArgumentCaptor.getValue();
        assertThat(booking).isEqualTo(finalBooking);
        assertThat(finalBooking.getStatus()).isEqualTo(EnumStatus.CANCELED);
        assertThat(bookingDtoResponse).isEqualTo(result);
    }

    @Test
    void approveOrRejectBookingByStranger() {
        // Не нашли бронирование по id
        //before
        Long bookingId = 1L;
        boolean approved = true;

        Booking booking = new Booking();
        User booker = new User();
        booker.setId(2L);
        booking.setBooker(booker);
        booking.setStatus(EnumStatus.WAITING);

        Item item = new Item();
        User itemOwner = new User();
        itemOwner.setId(1L);
        item.setOwner(itemOwner);
        booking.setItem(item);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());
        // Букинг по айди не найден
        //when
        assertThatThrownBy(() -> bookingService.approveOrRejectBooking(bookingId, 322L, approved))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(null);

        // Данные о бронировании доступны либо автору бронирования, либо владельцу вещи, к которой относится бронирование
        //before
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        //when
        assertThatThrownBy(() -> bookingService.approveOrRejectBooking(bookingId, 322L, approved))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(null);
        verify(userManager, only()).findUserById(322L);
    }

    @Test
    void approveOrRejectBookingNotWaitingStatus() {
        //before
        Long bookingId = 1L;
        Long userId = 2L;

        Booking booking = new Booking();
        User booker = new User();
        booker.setId(2L);
        booking.setBooker(booker);
        booking.setStatus(EnumStatus.REJECTED);

        Item item = new Item();
        User itemOwner = new User();
        itemOwner.setId(1L);
        item.setOwner(itemOwner);
        booking.setItem(item);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        //when
        assertThatThrownBy(() -> bookingService.approveOrRejectBooking(bookingId, userId, true))
                .isInstanceOf(NotAllowedException.class)
                .hasMessage(null);
        //then
        verify(userManager, only()).findUserById(userId);
    }

    @Test
    void findBookingById() {
    }

    @Test
    void findUsersBookings() {
    }

    @Test
    void findOwnersBookings() {
    }
}