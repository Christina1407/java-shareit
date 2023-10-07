package ru.practicum.shareit.dataJpa;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void searchItemsByNameAndDescription() {
        User user = userRepository.save(User.builder()
                .email("test@test.ru")
                .name("test")
                .build());
        //before
        Item item1 = itemRepository.save(Item.builder()
                .name("testName")
                .description("testDesc")
                .available(false)
                .owner(user)
                .build());
        Item item2 = itemRepository.save(Item.builder()
                .name("aaaaa")
                .description("uuuuiiiiooo")
                .available(true)
                .owner(user)
                .build());
        Item item3 = itemRepository.save(Item.builder()
                .name("nameTeSt")
                .description("description")
                .available(true)
                .owner(user)
                .build());
        Item item4 = itemRepository.save(Item.builder()
                .name("iiii")
                .description("kktesTld")
                .available(true)
                .owner(user)
                .build());
        List<Item> items = itemRepository.searchItemsByNameAndDescription("%test%", PageRequest.of(0, 1));
        assertThat(items.size()).isEqualTo(1);
        assertThat(items.get(0))
                .usingRecursiveComparison()
                .isEqualTo(item3);

        List<Item> items2 = itemRepository.searchItemsByNameAndDescription("%test%", PageRequest.of(0, 10));
        assertThat(items2.size()).isEqualTo(2);
        assertThat(items2.get(0))
                .usingRecursiveComparison()
                .isEqualTo(item3);
        assertThat(items2.get(1))
                .usingRecursiveComparison()
                .isEqualTo(item4);
    }
}