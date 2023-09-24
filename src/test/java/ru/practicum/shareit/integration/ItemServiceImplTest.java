package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.enums.EnumStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.item.comments.model.dto.CommentDtoRequest;
import ru.practicum.shareit.item.comments.model.dto.CommentDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDtoResponse;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemServiceImplTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void findUsersItems() {
        //before
        // Создаём юзера и айтемы
        User user = Instancio.create(User.class);
        user.setId(null);
        user = userRepository.save(user);
        Item item = Instancio.create(Item.class);
        item.setOwner(user);
        item.setId(null);
        item.setRequest(null);
        item = itemRepository.save(item);
        Item item1 = Instancio.create(Item.class);
        item1.setOwner(user);
        item1.setId(null);
        item1.setRequest(null);
        item1 = itemRepository.save(item1);
        //when
        List<ItemDtoResponse> usersItems = itemService.findUsersItems(user.getId(), PageRequest.of(0, 10));
        //then
        assertThat(usersItems.size()).isEqualTo(2);
        assertThat(usersItems.get(0).getId()).isEqualTo(item.getId());
        assertThat(usersItems.get(1).getId()).isEqualTo(item1.getId());
    }

    @Test
    void saveComment() {
        //before
        // Создаём юзера и айтемы
        User user = Instancio.create(User.class);
        user.setId(null);
        user = userRepository.save(user);
        Item item = Instancio.create(Item.class);
        item.setOwner(user);
        item.setId(null);
        item.setRequest(null);
        item = itemRepository.save(item);
        Item item1 = Instancio.create(Item.class);
        item1.setOwner(user);
        item1.setId(null);
        item1.setRequest(null);
        item1 = itemRepository.save(item1);
        // Создаём букинг
        Booking booking = bookingRepository.save(Instancio.of(Booking.class)
                .set(field(Booking::getBooker), user)
                .set(field(Booking::getItem), item)
                .set(field(Booking::getEndDate), LocalDateTime.now().minusDays(5))
                .set(field(Booking::getStartDate), LocalDateTime.now().minusDays(7))
                .set(field(Booking::getId), null)
                .set(field(Booking::getStatus), EnumStatus.APPROVED)
                .create());
        CommentDtoRequest commentDtoRequest = Instancio.create(CommentDtoRequest.class);
        //when
        CommentDtoResponse commentDtoResponse = itemService.saveComment(commentDtoRequest, item.getId(), user.getId());
        //then
        assertThat(commentDtoResponse).isNotNull();
        assertThat(commentDtoResponse.getText()).isEqualTo(commentDtoRequest.getText());
    }
}