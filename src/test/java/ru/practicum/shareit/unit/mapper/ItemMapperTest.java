package ru.practicum.shareit.unit.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.comments.model.dto.CommentDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemDtoRequest;
import ru.practicum.shareit.item.model.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.dto.ItemMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ItemMapperTest {
    private ItemMapper itemMapper;

    @BeforeEach
    void setUp() {
        itemMapper = new ItemMapper();
    }

    @Test
    void shouldBeSuccessfullyMapItemDtoResponse() {
        //before
        Item item = new Item();
        item.setId(1L);
        item.setName("test name");
        item.setDescription("test description");
        item.setAvailable(false);
        Booking lastBooking = new Booking();
        lastBooking.setId(1L);
        User user = new User();
        user.setId(1L);
        lastBooking.setBooker(user);
        Booking nextBooking = new Booking();
        nextBooking.setId(2L);
        nextBooking.setBooker(user);
        List<CommentDtoResponse> comments = new ArrayList<>();
        CommentDtoResponse commentDtoResponse = new CommentDtoResponse();
        comments.add(commentDtoResponse);

        //when
        ItemDtoResponse itemDtoResponse = itemMapper.map(item, lastBooking, nextBooking, comments);

        //then
        assertThat(itemDtoResponse.getId()).isEqualTo(1L);
        assertThat(itemDtoResponse.getName()).isEqualTo(item.getName());
        assertThat(itemDtoResponse.getDescription()).isEqualTo(item.getDescription());
        assertThat(itemDtoResponse.getAvailable()).isEqualTo(false);
        assertThat(itemDtoResponse.getComments().size()).isEqualTo(1);
        assertThat(itemDtoResponse.getLastBooking()).isNotNull();
        assertThat(itemDtoResponse.getLastBooking().getId()).isEqualTo(1L);
        assertThat(itemDtoResponse.getLastBooking().getBookerId()).isEqualTo(1L);
        assertThat(itemDtoResponse.getNextBooking()).isNotNull();
        assertThat(itemDtoResponse.getNextBooking().getBookerId()).isEqualTo(1L);
        assertThat(itemDtoResponse.getNextBooking().getId()).isEqualTo(nextBooking.getId());

        //when
        ItemDtoResponse itemDtoResponse2 = itemMapper.map(item, null, nextBooking, comments);

        //then
        assertThat(itemDtoResponse2.getLastBooking()).isNull();
        assertThat(itemDtoResponse2.getNextBooking()).isNotNull();
        assertThat(itemDtoResponse2.getNextBooking().getBookerId()).isEqualTo(1L);

        //when
        ItemDtoResponse itemDtoResponse3 = itemMapper.map(item, null, null, comments);

        //then
        assertThat(itemDtoResponse3.getLastBooking()).isNull();
        assertThat(itemDtoResponse3.getNextBooking()).isNull();
    }

    @Test
    void shouldBeSuccessfullyMapItemDto() {
        //before
        Item item = new Item();
        item.setId(1L);
        item.setName("test name");
        item.setDescription("test description");
        item.setAvailable(false);
        Request request = new Request();
        request.setId(1L);
        item.setRequest(request);

        //when
        ItemDto itemDto = itemMapper.map(item);

        //then
        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo("test name");
        assertThat(itemDto.getDescription()).isEqualTo(item.getDescription());
        assertThat(itemDto.getAvailable()).isEqualTo(false);
        assertThat(itemDto.getRequestId()).isEqualTo(1L);

        //before
        item.setRequest(null);
        //when
        ItemDto itemDto2 = itemMapper.map(item);

        //then
        assertThat(itemDto2.getRequestId()).isNull();
    }

    @Test
    void shouldBeSuccessfullyMapItem() {
        //before
        ItemDtoRequest itemDtoRequest = new ItemDtoRequest(1L, "name", "description", false, null);
        User user = new User();
        Request request = new Request();

        //when
        Item item = itemMapper.map(itemDtoRequest, user, request);

        //then
        assertThat(item.getId()).isEqualTo(1L);
        assertThat(item.getName()).isEqualTo("name");
        assertThat(item.getDescription()).isEqualTo("description");
        assertThat(item.getAvailable()).isEqualTo(false);
        assertThat(item.getOwner()).isEqualTo(user);
        assertThat(item.getRequest()).isEqualTo(request);
    }

}