package ru.practicum.shareit.request.model.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemMapper;
import ru.practicum.shareit.request.model.Request;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestMapperTest {
    private RequestMapper requestMapper;
    @Mock
    private ItemMapper itemMapper;
    @BeforeEach
    void setUp() {
        requestMapper = new RequestMapper(itemMapper);
    }

    @Test
    void shouldBeSuccessfullyMapRequestDtoResponse() {
        //before
        Request request = new Request();
        List<Item> itemList = new ArrayList<>();
        Item item = new Item();
        itemList.add(item);
        request.setId(1L);
        request.setItems(itemList);
        request.setDescription("test");
        LocalDateTime localDateTime = LocalDateTime.now();
        request.setCreatedDate(localDateTime);
        //when
        RequestDtoResponse requestDtoResponse = requestMapper.map(request, false);
        //then
        assertThat(requestDtoResponse).isNotNull();
        assertThat(requestDtoResponse.getCreated()).isEqualTo(localDateTime);
        assertThat(requestDtoResponse.getItems()).isNull();
        assertThat(requestDtoResponse.getDescription()).isEqualTo("test");
        assertThat(requestDtoResponse.getId()).isEqualTo(1L);

        //before
        List<ItemDto> itemDtoList = new ArrayList<>();
        when(itemMapper.map(itemList)).thenReturn(itemDtoList);
        //when
        RequestDtoResponse requestDtoResponse2 = requestMapper.map(request, true);
        //then
        verify(itemMapper, only()).map(request.getItems());
        assertThat(requestDtoResponse2.getItems()).isEqualTo(itemDtoList);
    }
}