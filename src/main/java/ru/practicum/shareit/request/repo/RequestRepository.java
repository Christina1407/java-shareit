package ru.practicum.shareit.request.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByRequester_IdOrderByCreatedDateDesc(Long requesterId);
    List<Request> findByRequester_IdNotOrderByCreatedDateDesc(Long userId, Pageable pageable);
}
