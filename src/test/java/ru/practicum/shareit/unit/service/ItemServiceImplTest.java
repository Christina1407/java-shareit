package ru.practicum.shareit.unit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.enums.EnumStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.exception.NotAllowedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comments.model.Comment;
import ru.practicum.shareit.item.comments.model.dto.CommentDtoRequest;
import ru.practicum.shareit.item.comments.model.dto.CommentDtoResponse;
import ru.practicum.shareit.item.comments.model.dto.CommentMapper;
import ru.practicum.shareit.item.comments.repo.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemDtoRequest;
import ru.practicum.shareit.item.model.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.dto.ItemMapper;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.manager.ItemManager;
import ru.practicum.shareit.manager.UserManager;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repo.RequestRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    private ItemService itemService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private UserManager userManager;
    @Mock
    private ItemManager itemManager;

    @Captor
    ArgumentCaptor<List<Long>> itemIdsArgumentCaptor;
    @Captor
    ArgumentCaptor<Item> itemArgumentCaptor;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(itemRepository, itemMapper, commentMapper, userManager, itemManager,
                commentRepository, bookingRepository, requestRepository);
    }

    @Test
    void saveItemWithoutRequest() {
        //before
        ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .build();
        Long ownerId = 1L;
        User owner = new User();

        ItemDto itemDto = ItemDto.builder().build();
        when(userManager.findUserById(ownerId)).thenReturn(owner);
        Item item = new Item();
        when(itemMapper.map(itemDtoRequest, owner, null)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.map(item)).thenReturn(itemDto);

        //when
        ItemDto result = itemService.saveItem(itemDtoRequest, ownerId);

        //then
        assertThat(result).isEqualTo(itemDto);
        verify(itemMapper, times(1)).map(itemDtoRequest, owner, null);
        verify(userManager, only()).findUserById(ownerId);
    }

    @Test
    void saveItemWithRequest() {
        //before
        ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .requestId(1L)
                .build();
        Long ownerId = 1L;
        User owner = new User();

        ItemDto itemDto = ItemDto.builder().build();
        when(userManager.findUserById(ownerId)).thenReturn(owner);
        Item item = new Item();
        Request request = new Request();
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(itemMapper.map(itemDtoRequest, owner, request)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.map(item)).thenReturn(itemDto);

        //when
        ItemDto result = itemService.saveItem(itemDtoRequest, ownerId);

        //then
        assertThat(result).isEqualTo(itemDto);
        verify(itemMapper, times(1)).map(itemDtoRequest, owner, request);
        verify(userManager, only()).findUserById(ownerId);
    }

    @Test
    void saveItemWithRequestNotFound() {
        //before
        ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .requestId(100500L)
                .build();
        Long ownerId = 1L;
        User owner = new User();

        ItemDto itemDto = ItemDto.builder().build();
        when(userManager.findUserById(ownerId)).thenReturn(owner);
        Item item = new Item();
        when(requestRepository.findById(100500L)).thenReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> itemService.saveItem(itemDtoRequest, ownerId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(null);
    }

    @Test
    void saveCommentSuccess() {
        //before
        //Создаём входную модель для тестового метода
        CommentDtoRequest commentDtoRequest = CommentDtoRequest.builder()
                .text("test")
                .build();
        Long itemId = 10L;
        Long userId = 1L;
        //Создаём мокирование
        User user = new User();
        when(userManager.findUserById(userId)).thenReturn(user);
        Item item = new Item();
        item.setId(10L);
        when(itemManager.findItemById(itemId)).thenReturn(item);
        //Проверяем, что есть завершившееся бронирование
        when(bookingRepository.existsByItem_IdAndStatusInAndBooker_IdAndEndDateLessThanEqual(eq(10L),
                eq(new ArrayList<>(List.of(EnumStatus.APPROVED))), eq(1L), any())).thenReturn(true);
        Comment comment = new Comment();
        when(commentMapper.map(commentDtoRequest, user, item)).thenReturn(comment);
        Comment savedComment = new Comment();
        when(commentRepository.save(comment)).thenReturn(savedComment);
        CommentDtoResponse commentDtoResponse = new CommentDtoResponse();
        when(commentMapper.map(savedComment)).thenReturn(commentDtoResponse);

        //when
        CommentDtoResponse result = itemService.saveComment(commentDtoRequest, itemId, userId);
        //then
        assertThat(result).isEqualTo(commentDtoResponse);
        verify(commentMapper, times(1)).map(commentDtoRequest, user, item);
    }

    @Test
    void saveCommentWithoutPastBookings() {
        //before
        //Создаём входную модель для тестового метода
        CommentDtoRequest commentDtoRequest = CommentDtoRequest.builder()
                .text("test")
                .build();
        Long itemId = 10L;
        Long userId = 1L;
        //Создаём мокирование
        User user = new User();
        when(userManager.findUserById(userId)).thenReturn(user);
        Item item = new Item();
        item.setId(10L);
        when(itemManager.findItemById(itemId)).thenReturn(item);
        //Проверяем, что нет завершившихся бронирований
        when(bookingRepository.existsByItem_IdAndStatusInAndBooker_IdAndEndDateLessThanEqual(eq(10L),
                eq(new ArrayList<>(List.of(EnumStatus.APPROVED))), eq(1L), any())).thenReturn(false);
        //when
        assertThatThrownBy(() -> itemService.saveComment(commentDtoRequest, itemId, userId))
                .isInstanceOf(NotAllowedException.class);
    }

    @Test
    void updateItemByStranger() {
        //before
        //Создаём входную модель для тестового метода
        ItemDtoRequest itemDtoRequest = Instancio.of(ItemDtoRequest.class)
                .set(field(ItemDtoRequest::getId), 1L)
                .create();
        User stranger = Instancio.of(User.class)
                .set(field(User::getItems), Instancio.ofList(Item.class)
                        .size(1)
                        .set(field(Item::getId), 2L)
                        .create())
                .create();
        //Создаём мокирование
        when(userManager.findUserById(stranger.getId())).thenReturn(stranger);
        //when
        //Проверяем, что вещь для обновления не принадлежит юзеру, чей айдишник прилетел
        assertThatThrownBy(() -> itemService.updateItem(itemDtoRequest, stranger.getId()))
                .isInstanceOf(NotFoundException.class);
        verify(itemManager, only()).findItemById(itemDtoRequest.getId());
    }

    @Test
    void updateItemByOwner() throws JsonProcessingException {
        //before
        //Создаём входную модель для тестового метода
        ItemDtoRequest itemDtoRequest = Instancio.of(ItemDtoRequest.class)
                .create();
        User user = Instancio.of(User.class)
                .set(field(User::getItems), Instancio.ofList(Item.class)
                        .size(1)
                        .set(field(Item::getId), itemDtoRequest.getId())
                        .create())
                .create();
        objectMapper.findAndRegisterModules();
        Item initialItem = objectMapper
                .readValue(objectMapper.writeValueAsString(user.getItems().get(0)), Item.class);
        when(userManager.findUserById(user.getId())).thenReturn(user);
        Item savedItem = new Item();
        when(itemRepository.save(user.getItems().get(0))).thenReturn(savedItem);
        ItemDto itemDto = Instancio.create(ItemDto.class);
        when(itemMapper.map(savedItem)).thenReturn(itemDto);
        //when
        ItemDto result = itemService.updateItem(itemDtoRequest, user.getId());
        //then
        assertThat(result).isEqualTo(itemDto);
        verify(itemRepository, only()).save(itemArgumentCaptor.capture());
        verify(itemMapper, only()).map(savedItem);
        Item itemUpdated = itemArgumentCaptor.getValue();
        assertThat(itemUpdated.getName()).isEqualTo(itemDtoRequest.getName());
        assertThat(itemUpdated.getDescription()).isEqualTo(itemDtoRequest.getDescription());
        assertThat(itemUpdated.getAvailable()).isEqualTo(itemDtoRequest.getAvailable());
        assertThat(itemUpdated.getOwner()).usingRecursiveComparison()
                .isEqualTo(initialItem.getOwner());
        assertThat(itemUpdated.getComments()).usingRecursiveComparison()
                .isEqualTo(initialItem.getComments());
        assertThat(itemUpdated.getRequest()).usingRecursiveComparison()
                .isEqualTo(initialItem.getRequest());
    }

    @Test
    void findItemByIdByOwner() {
        //before
        //Создаём входную модель для тестового метода
        Long itemId = 1L;
        Long userId = 2L;
        Item item = Instancio.create(Item.class);
        item.getOwner().setId(userId);
        item.setId(itemId);
        when(itemManager.findItemById(itemId)).thenReturn(item);
        Booking lastBooking = new Booking();
        Booking nextBooking = new Booking();
        when(bookingRepository.findTopByItem_IdAndStartDateLessThanEqualAndStatusNotInOrderByStartDateDesc(eq(item.getId()),
                any(), eq(List.of(EnumStatus.CANCELED, EnumStatus.REJECTED)))).thenReturn(lastBooking);
        when(bookingRepository.findTopByItem_IdAndStartDateGreaterThanEqualAndStatusNotInOrderByStartDate(eq(item.getId()),
                any(), eq(List.of(EnumStatus.CANCELED, EnumStatus.REJECTED)))).thenReturn(nextBooking);
        List<CommentDtoResponse> responseList = new ArrayList<>();
        ItemDtoResponse itemDtoResponse = Instancio.create(ItemDtoResponse.class);
        when(commentMapper.map(item.getComments())).thenReturn(responseList);
        when(itemMapper.map(item, lastBooking, nextBooking, responseList)).thenReturn(itemDtoResponse);
        //when
        ItemDtoResponse result = itemService.findItemById(itemId, userId);
        //then
        assertThat(result).isEqualTo(itemDtoResponse);
        verify(userManager, only()).findUserById(userId);
        verify(itemMapper, only()).map(item, lastBooking, nextBooking, responseList);
        verify(commentMapper, only()).map(item.getComments());
    }

    @Test
    void findItemByIdByStranger() {
        //before
        //Создаём входную модель для тестового метода
        Long itemId = 1L;
        Long userId = 2L;
        Item item = Instancio.create(Item.class);
        item.getOwner().setId(userId);
        item.setId(itemId);
        when(itemManager.findItemById(itemId)).thenReturn(item);
        List<CommentDtoResponse> responseList = new ArrayList<>();
        ItemDtoResponse itemDtoResponse = Instancio.create(ItemDtoResponse.class);
        when(commentMapper.map(item.getComments())).thenReturn(responseList);
        when(itemMapper.map(item, null, null, responseList)).thenReturn(itemDtoResponse);
        //when
        ItemDtoResponse result = itemService.findItemById(itemId, userId);
        //then
        assertThat(result).isEqualTo(itemDtoResponse);
        verify(userManager, only()).findUserById(userId);
        verify(itemMapper, only()).map(item, null, null, responseList);
        verify(commentMapper, only()).map(item.getComments());
    }

    @Test
    void searchItemsTextIsBlank() {
        //before
        //Создаём входную модель для тестового метода
        String text = " ";
        Long renterId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        //when
        List<ItemDto> searchItems = itemService.searchItems(text, renterId, pageable);
        //then
        assertThat(searchItems).isEmpty();
        verify(itemRepository, never()).searchItemsByNameAndDescription(any(), any());
    }
}