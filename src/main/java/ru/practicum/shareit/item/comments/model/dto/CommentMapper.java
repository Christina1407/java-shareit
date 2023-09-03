package ru.practicum.shareit.item.comments.model.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.comments.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentMapper {
    public List<CommentDtoResponse> map(List<Comment> comments) {
        return comments.stream()
                .map(this::getBuild)
                .collect(Collectors.toList());
    }

    private CommentDtoResponse getBuild(Comment comment) {
        return CommentDtoResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreatedDate())
                .build();
    }

    private Comment getBuild(CommentDtoRequest commentDtoRequest, User author, Item item) {
        return Comment.builder()
                .text(commentDtoRequest.getText())
                .author(author)
                .item(item)
                .build();
    }

    public CommentDtoResponse map(Comment comment) {
        return getBuild(comment);
    }

    public Comment map(CommentDtoRequest commentDtoRequest, User author, Item item) {
        return getBuild(commentDtoRequest, author, item);
    }
}
