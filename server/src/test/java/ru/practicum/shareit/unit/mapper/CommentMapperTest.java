package ru.practicum.shareit.unit.mapper;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.comments.model.Comment;
import ru.practicum.shareit.item.comments.model.dto.CommentDtoRequest;
import ru.practicum.shareit.item.comments.model.dto.CommentDtoResponse;
import ru.practicum.shareit.item.comments.model.dto.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CommentMapperTest {

    private CommentMapper commentMapper;

    @BeforeEach
    void setUp() {
        commentMapper = new CommentMapper();
    }

    @Test
    void shouldBeSuccessfullyMapComment() {
        //before
        Comment comment = Instancio.create(Comment.class);
        //when
        CommentDtoResponse mapped = commentMapper.map(comment);
        //then
        assertThat(mapped.getId()).isEqualTo(comment.getId());
        assertThat(mapped.getText()).isEqualTo(comment.getText());
        assertThat(mapped.getAuthorName()).isEqualTo(comment.getAuthor().getName());
        assertThat(mapped.getCreated()).isEqualTo(comment.getCreatedDate());
    }

    @Test
    void shouldBeSuccessfullyMapCommentDtoRequest() {
        //before
        CommentDtoRequest commentDtoRequest = Instancio.create(CommentDtoRequest.class);
        User author = Instancio.create(User.class);
        Item item = Instancio.create(Item.class);
        //when
        Comment mapped = commentMapper.map(commentDtoRequest, author, item);
        //then
        assertThat(mapped.getId()).isNull();
        assertThat(mapped.getText()).isEqualTo(commentDtoRequest.getText());
        assertThat(mapped.getAuthor()).isEqualTo(author);
        assertThat(mapped.getItem()).isEqualTo(item);
    }

    @Test
    void shouldBeSuccessfullyMapCommentsList() {
        //before
        List<Comment> commentList = Instancio.ofList(Comment.class)
                .size(2)
                .create();
        //when
        List<CommentDtoResponse> mapped = commentMapper.map(commentList);
        //then
        assertThat(mapped.size()).isEqualTo(2);
        for (int i = 0; i < mapped.size(); i++) {
            assertThat(mapped.get(i).getId()).isEqualTo(commentList.get(i).getId());
            assertThat(mapped.get(i).getText()).isEqualTo(commentList.get(i).getText());
            assertThat(mapped.get(i).getAuthorName()).isEqualTo(commentList.get(i).getAuthor().getName());
            assertThat(mapped.get(i).getCreated()).isEqualTo(commentList.get(i).getCreatedDate());
        }
    }
}