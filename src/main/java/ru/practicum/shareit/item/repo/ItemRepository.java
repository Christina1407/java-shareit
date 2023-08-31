package ru.practicum.shareit.item.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("from Item as i where (lower(i.name) like lower(?1) or lower(i.description) like lower(?1)) and i.available is true")
    List<Item> searchItemsByNameAndDescription(String text);
}
