package ru.practicum.shareit.unit.mapper;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.model.dto.RequestDto;
import ru.practicum.shareit.request.model.dto.RequestDtoResponse;
import ru.practicum.shareit.request.model.dto.RequestMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    void shouldBeSuccessfullyMapRequestList() {
        //before
        List<Request> requestList = Instancio.ofList(Request.class)
                .size(2)
                .create();
        List<ItemDto> itemDtoList = Instancio.ofList(ItemDto.class)
                        .size(3)
                                .create();
        when(itemMapper.map(requestList.get(0).getItems())).thenReturn(itemDtoList);
        when(itemMapper.map(requestList.get(1).getItems())).thenReturn(itemDtoList);
        //when
        List<RequestDtoResponse> mapped = requestMapper.map(requestList, true);
        //then
        assertThat(mapped.size()).isEqualTo(2);
        for (int i = 0; i < mapped.size(); i++) {
            assertThat(mapped.get(i).getId()).isEqualTo(requestList.get(i).getId());
            assertThat(mapped.get(i).getCreated()).isEqualTo(requestList.get(i).getCreatedDate());
            assertThat(mapped.get(i).getDescription()).isEqualTo(requestList.get(i).getDescription());
            assertThat(mapped.get(i).getItems().size()).isEqualTo(3);
        }
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

    @Test
    void shouldBeSuccessfullyMapRequest() {
        //before
        RequestDto requestDto = new RequestDto();
        User user = new User();
        requestDto.setDescription("test");
        //when
        Request request = requestMapper.map(requestDto, user);
        //then
        assertThat(request).isNotNull();
        assertThat(request.getDescription()).isEqualTo("test");
        assertThat(request.getRequester()).isEqualTo(user);
        assertThat(request.getCreatedDate()).isNull();
        assertThat(request.getId()).isNull();
    }
}