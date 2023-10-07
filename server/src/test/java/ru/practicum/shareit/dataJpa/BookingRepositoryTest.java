package ru.practicum.shareit.dataJpa;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.enums.EnumStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void findCurrentBookings() {
        //before
        //Создаём букера
        User user = userRepository.save(User.builder()
                .name("test")
                .email("test@test.ru")
                .build());
        //Создаём owner
        User owner = userRepository.save(User.builder()
                .name("owner")
                .email("testOwner@test.ru")
                .build());
        //Создаём вещь
        Item item = itemRepository.save(Item.builder()
                .name("tetsItem")
                .description("testDesc")
                .available(true)
                .owner(owner)
                .build());
        //Создаём бронирования
        Booking booking = bookingRepository.save(Booking.builder()
                .item(item)
                .booker(user)
                .status(EnumStatus.APPROVED)
                .startDate(LocalDateTime.now().minusDays(2))
                .endDate(LocalDateTime.now().plusDays(2))
                .build());
        bookingRepository.save(Booking.builder()
                .item(item)
                .booker(user)
                .status(EnumStatus.WAITING)
                .startDate(LocalDateTime.now().plusDays(2))
                .endDate(LocalDateTime.now().plusDays(5))
                .build());
        LocalDateTime localDateTime = LocalDateTime.now();
        Booking booking3 = bookingRepository.save(Booking.builder()
                .item(item)
                .booker(user)
                .status(EnumStatus.REJECTED)
                .startDate(localDateTime)
                .endDate(LocalDateTime.now().plusDays(5))
                .build());
        List<Booking> currentBookings = bookingRepository.findCurrentBookings(user.getId(), localDateTime, PageRequest.of(0, 10));
        assertThat(currentBookings.size()).isEqualTo(2);
        assertThat(currentBookings.get(0))
                .usingRecursiveComparison()
                .isEqualTo(booking3);
        assertThat(currentBookings.get(1))
                .usingRecursiveComparison()
                .isEqualTo(booking);
    }

    @Test
    void findCurrentOwnerBookings() {
        //before
        //Создаём owner
        User owner = userRepository.save(User.builder()
                .name("test")
                .email("test@test.ru")
                .build());
        //Создаём букера
        User user = userRepository.save(User.builder()
                .name("booker")
                .email("booker@test.ru")
                .build());
        //Создаём вещи
        Item item = itemRepository.save(Item.builder()
                .name("tetsItem")
                .description("testDesc")
                .available(true)
                .owner(owner)
                .build());
        Item item2 = itemRepository.save(Item.builder()
                .name("tetsItem2")
                .description("testDesc")
                .available(true)
                .owner(owner)
                .build());
        //Создаём бронирования
        Booking booking = bookingRepository.save(Booking.builder()
                .item(item2)
                .booker(user)
                .status(EnumStatus.APPROVED)
                .startDate(LocalDateTime.now().minusDays(2))
                .endDate(LocalDateTime.now().plusDays(2))
                .build());
        bookingRepository.save(Booking.builder()
                .item(item)
                .booker(user)
                .status(EnumStatus.WAITING)
                .startDate(LocalDateTime.now().plusDays(2))
                .endDate(LocalDateTime.now().plusDays(5))
                .build());
        LocalDateTime localDateTime = LocalDateTime.now();
        Booking booking3 = bookingRepository.save(Booking.builder()
                .item(item)
                .booker(user)
                .status(EnumStatus.REJECTED)
                .startDate(localDateTime)
                .endDate(LocalDateTime.now().plusDays(5))
                .build());
        List<Booking> currentBookings = bookingRepository.findCurrentBookings(user.getId(), localDateTime, PageRequest.of(0, 10));
        assertThat(currentBookings.size()).isEqualTo(2);
        assertThat(currentBookings.get(0))
                .usingRecursiveComparison()
                .isEqualTo(booking3);
        assertThat(currentBookings.get(1))
                .usingRecursiveComparison()
                .isEqualTo(booking);
    }
}