package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.enums.EnumStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotAllowedException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.manager.UserManager;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingServiceImplTest {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserManager userManager;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {

    }

    @Test
    void saveBooking() {
        //before
        //Создаем владельца вещи
        User user = new User();
        user.setName("test");
        user.setEmail("test@email.ru");
        user = userRepository.save(user);

        //Создаём вещь
        Item item = new Item();
        item.setOwner(user);
        item.setAvailable(true);
        item.setName("testItem");
        item.setDescription("testDescription");
        item = itemRepository.save(item);

        //Создаём владельца первого бронирования
        User user2 = new User();
        user2.setName("test2");
        user2.setEmail("test2@email.ru");
        user2 = userRepository.save(user2);

        //Создаём на вещь бронирование номер 1
        Booking booking1 = new Booking();
        booking1.setItem(item);
        booking1.setStatus(EnumStatus.APPROVED);
        booking1.setStartDate(LocalDateTime.now().minusDays(1));
        booking1.setEndDate(LocalDateTime.now().plusDays(1));
        booking1.setBooker(user2);
        booking1 = bookingRepository.save(booking1);

        //Создаём владельца второго бронирования
        User user3 = new User();
        user3.setName("test3");
        user3.setEmail("test3@email.ru");
        user3 = userRepository.save(user3);

        //Создаём на вещь второе бронирование с пересечением по времени
        //before
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(
                LocalDateTime.now(), LocalDateTime.now().plusDays(3), item.getId());
        //then
        User finalUser = user3;
        assertThatThrownBy(() -> bookingService.saveBooking(bookingDtoRequest, finalUser.getId()))
                .isInstanceOf(NotAllowedException.class);
    }
}