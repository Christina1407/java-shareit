package ru.practicum.shareit.item.comments.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.comments.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
