package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDtoRequest;
import ru.practicum.shareit.item.model.dto.ItemDtoResponse;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
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
}