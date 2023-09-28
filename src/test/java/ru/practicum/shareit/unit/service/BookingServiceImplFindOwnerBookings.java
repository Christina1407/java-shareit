package ru.practicum.shareit.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.enums.EnumState;
import ru.practicum.shareit.booking.enums.EnumStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.dto.BookingMapper;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.manager.ItemManager;
import ru.practicum.shareit.manager.UserManager;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplFindOwnerBookings {
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
    private ArgumentCaptor<List<Long>> itemsArgumentCaptor;
    private Long ownerId;
    private EnumState enumState;
    private Pageable pageable;
    private User user;
    private Item item1;
    private Item item2;
    private List<Booking> bookings;
    private List<BookingDtoResponse> responseList;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(bookingRepository, bookingMapper, userManager, itemManager);
        ownerId = 1L;
        pageable = Pageable.ofSize(1);
        user = new User();
        item1 = new Item();
        item1.setId(101L);
        item2 = new Item();
        item2.setId(102L);
        user.setItems(new ArrayList<>(List.of(item1, item2)));
        when(userManager.findUserById(ownerId)).thenReturn(user);
        bookings = new ArrayList<>();
        responseList = new ArrayList<>();
        when(bookingMapper.map(bookings)).thenReturn(responseList);
    }

    @Test
    void findOwnersBookingsSuccessAll() {
        //before
        enumState = EnumState.ALL;
        when(bookingRepository.findByItem_IdInOrderByStartDateDesc(any(), eq(pageable))).thenReturn(bookings);
        //when
        List<BookingDtoResponse> ownersBookings = bookingService.findOwnersBookings(ownerId, enumState, pageable);
        //then
        assertThat(responseList).isEqualTo(ownersBookings);
        verify(bookingRepository, only()).findByItem_IdInOrderByStartDateDesc(itemsArgumentCaptor.capture(), eq(pageable));
        assertThat(2).isEqualTo(itemsArgumentCaptor.getValue().size());
        List<Long> longList = itemsArgumentCaptor.getValue();
        assertThat(new ArrayList<>(List.of(101L, 102L)))
                .usingRecursiveComparison()
                .isEqualTo(longList);
        verify(bookingMapper, only()).map(bookings);
    }

    @Test
    void findOwnersBookingsSuccessPast() {
        //before
        enumState = EnumState.PAST;
        when(bookingRepository.findByItem_IdInAndEndDateLessThanEqualOrderByStartDateDesc(any(), any(), eq(pageable))).thenReturn(bookings);
        //when
        List<BookingDtoResponse> ownersBookings = bookingService.findOwnersBookings(ownerId, enumState, pageable);
        //then
        assertThat(responseList).isEqualTo(ownersBookings);
        verify(bookingRepository, only()).findByItem_IdInAndEndDateLessThanEqualOrderByStartDateDesc(itemsArgumentCaptor.capture(), any(), eq(pageable));
        assertThat(2).isEqualTo(itemsArgumentCaptor.getValue().size());
        List<Long> longList = itemsArgumentCaptor.getValue();
        assertThat(new ArrayList<>(List.of(101L, 102L)))
                .usingRecursiveComparison()
                .isEqualTo(longList);
        verify(bookingMapper, only()).map(bookings);
    }

    @Test
    void findOwnersBookingsSuccessCurrent() {
        //before
        enumState = EnumState.CURRENT;
        when(bookingRepository.findCurrentOwnerBookings(any(), any(), eq(pageable))).thenReturn(bookings);
        //when
        List<BookingDtoResponse> ownersBookings = bookingService.findOwnersBookings(ownerId, enumState, pageable);
        //then
        assertThat(responseList).isEqualTo(ownersBookings);
        verify(bookingRepository, only()).findCurrentOwnerBookings(itemsArgumentCaptor.capture(), any(), eq(pageable));
        assertThat(2).isEqualTo(itemsArgumentCaptor.getValue().size());
        List<Long> longList = itemsArgumentCaptor.getValue();
        assertThat(new ArrayList<>(List.of(101L, 102L)))
                .usingRecursiveComparison()
                .isEqualTo(longList);
        verify(bookingMapper, only()).map(bookings);
    }

    @Test
    void findOwnersBookingsSuccessFuture() {
        //before
        enumState = EnumState.FUTURE;
        when(bookingRepository.findByItem_IdInAndStartDateGreaterThanEqualOrderByStartDateDesc(any(), any(), eq(pageable))).thenReturn(bookings);
        //when
        List<BookingDtoResponse> ownersBookings = bookingService.findOwnersBookings(ownerId, enumState, pageable);
        //then
        assertThat(responseList).isEqualTo(ownersBookings);
        verify(bookingRepository, only()).findByItem_IdInAndStartDateGreaterThanEqualOrderByStartDateDesc(
                itemsArgumentCaptor.capture(), any(), eq(pageable));
        assertThat(2).isEqualTo(itemsArgumentCaptor.getValue().size());
        List<Long> longList = itemsArgumentCaptor.getValue();
        assertThat(new ArrayList<>(List.of(101L, 102L)))
                .usingRecursiveComparison()
                .isEqualTo(longList);
        verify(bookingMapper, only()).map(bookings);
    }

    @Test
    void findOwnersBookingsSuccessWaiting() {
        //before
        enumState = EnumState.WAITING;
        when(bookingRepository.findByItem_IdInAndStatusInOrderByStartDateDesc(any(), eq(List.of(EnumStatus.WAITING)), eq(pageable))).thenReturn(bookings);
        //when
        List<BookingDtoResponse> ownersBookings = bookingService.findOwnersBookings(ownerId, enumState, pageable);
        //then
        assertThat(responseList).isEqualTo(ownersBookings);
        verify(bookingRepository, only()).findByItem_IdInAndStatusInOrderByStartDateDesc(
                itemsArgumentCaptor.capture(), eq(List.of(EnumStatus.WAITING)), eq(pageable));
        assertThat(2).isEqualTo(itemsArgumentCaptor.getValue().size());
        List<Long> longList = itemsArgumentCaptor.getValue();
        assertThat(new ArrayList<>(List.of(101L, 102L)))
                .usingRecursiveComparison()
                .isEqualTo(longList);
        verify(bookingMapper, only()).map(bookings);
    }

    @Test
    void findOwnersBookingsSuccessRejected() {
        //before
        enumState = EnumState.REJECTED;
        when(bookingRepository.findByItem_IdInAndStatusInOrderByStartDateDesc(any(),
                eq(List.of(EnumStatus.REJECTED, EnumStatus.CANCELED)), eq(pageable))).thenReturn(bookings);
        //when
        List<BookingDtoResponse> ownersBookings = bookingService.findOwnersBookings(ownerId, enumState, pageable);
        //then
        assertThat(responseList).isEqualTo(ownersBookings);
        verify(bookingRepository, only()).findByItem_IdInAndStatusInOrderByStartDateDesc(itemsArgumentCaptor.capture(),
                eq(List.of(EnumStatus.REJECTED, EnumStatus.CANCELED)), eq(pageable));
        assertThat(2).isEqualTo(itemsArgumentCaptor.getValue().size());
        List<Long> longList = itemsArgumentCaptor.getValue();
        assertThat(new ArrayList<>(List.of(101L, 102L)))
                .usingRecursiveComparison()
                .isEqualTo(longList);
        verify(bookingMapper, only()).map(bookings);
    }
}
