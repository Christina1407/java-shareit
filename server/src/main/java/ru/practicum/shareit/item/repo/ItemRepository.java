package ru.practicum.shareit.item.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("from Item as i where (lower(i.name) like lower(?1) or lower(i.description) like lower(?1)) and i.available is true order by i.id")
    List<Item> searchItemsByNameAndDescription(String text, Pageable pageable);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "item_comments")
    List<Item> findByOwner_Id(Long ownerId, Pageable pageable);
}
