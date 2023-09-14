package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

public interface RequestRepository extends JpaRepository<Request, Long> {
}
