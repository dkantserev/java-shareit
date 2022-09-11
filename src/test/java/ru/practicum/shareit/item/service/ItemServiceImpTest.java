package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.exception.BookingException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.storage.CommentStorage;
import ru.practicum.shareit.item.MapperItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoUpdate;
import ru.practicum.shareit.item.itemException.ItemNotFound;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.storage.RequestStorage;
import ru.practicum.shareit.user.UserExceptions.UserNotFound;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.userSevice.UserServiceImp;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImpTest {

    @Mock
    ItemStorage itemStorage;
    @Mock
    UserServiceImp userServiceImp;
    @Mock
    BookingStorage bookingStorage;
    @Mock
    CommentStorage commentStorage;
    @Mock
    RequestStorage requestStorage;
    @InjectMocks
    ItemServiceImp itemServiceImp;

    User user = new User();
    UserDto userDto = UserDto.builder().build();
    ItemRequest request = new ItemRequest();
    Item item = new Item();
    ItemDto itemDto = ItemDto.builder().build();

    @Test
    public void thenAdd_WhenPositive() {

        itemDto.setRequestId(2L);
        Mockito
                .when(userServiceImp.get(1L))
                .thenReturn(userDto);
        Mockito
                .when(requestStorage.findById(2L))
                .thenReturn(Optional.ofNullable(request));
        Mockito
                .when(itemStorage.save(item))
                .thenReturn(item);
        try (MockedStatic<MapperItemDto> mapper = Mockito.mockStatic(MapperItemDto.class)) {
            mapper.when(() -> MapperItemDto.toItemDto(item)).thenReturn(itemDto);
            mapper.when(() -> MapperItemDto.toItem(itemDto)).thenReturn(item);
            itemServiceImp.add(Optional.ofNullable(itemDto), Optional.of(1L));
            Mockito.verify(itemStorage, Mockito.times(1)).save(item);
        }

    }


    @Test
    public void thenAddAndUserNotFound_WhenThrow() {

        itemDto.setRequestId(2L);
        Mockito
                .when(userServiceImp.get(1L))
                .thenReturn(null);

        try (MockedStatic<MapperItemDto> mapper = Mockito.mockStatic(MapperItemDto.class)) {
            mapper.when(() -> MapperItemDto.toItemDto(item)).thenReturn(itemDto);
            mapper.when(() -> MapperItemDto.toItem(itemDto)).thenReturn(item);

            assertThrows(RuntimeException.class, () -> itemServiceImp.add(Optional.ofNullable(itemDto), Optional.of(1L)));
        }
    }

    @Test
    public void whenEmptyArgument_thenException() {

        Optional<Long> id = Optional.empty();
        Optional<ItemDto> i = Optional.empty();
        assertThrows(RuntimeException.class, () -> itemServiceImp.add(i, id));

    }

    @Test
    public void whenUpdateNotFoundItem_thenException() {

        Optional<Item> i = Optional.empty();
        ItemDtoUpdate id = new ItemDtoUpdate();
        Optional<Long> userId = Optional.of(1L);
        Optional<Long> itemId = Optional.of(1L);
        Mockito
                .when(itemStorage.findById(1L))
                .thenReturn(i);
        assertThrows(BookingException.class, () -> itemServiceImp.update(id, userId, itemId));
    }

    @Test
    public void whenUpdateNotFoundUser_thenException() {

        Item i = new Item();
        ItemDtoUpdate id = new ItemDtoUpdate();
        Optional<Long> userId = Optional.of(1L);
        Optional<Long> itemId = Optional.of(1L);
        Mockito
                .when(itemStorage.findById(1L))
                .thenReturn(Optional.of(i));
        Mockito
                .when(userServiceImp.get(1L))
                .thenReturn(null);
        assertThrows(RuntimeException.class, () -> itemServiceImp.update(id, userId, itemId));
    }

    @Test
    public void whenUpdateNoOwner_thenException() {

        Item i = new Item();
        i.setOwner(2L);
        ItemDtoUpdate id = new ItemDtoUpdate();
        Optional<Long> userId = Optional.of(1L);
        Optional<Long> itemId = Optional.of(1L);
        Mockito
                .when(itemStorage.findById(1L))
                .thenReturn(Optional.of(i));
        Mockito
                .when(userServiceImp.get(1L))
                .thenReturn(userDto);
        assertThrows(UserNotFound.class, () -> itemServiceImp.update(id, userId, itemId));
    }

    @Test
    public void whenEmptyParam_whenException() {

        ItemDtoUpdate id = new ItemDtoUpdate();
        Optional<Long> userId = Optional.empty();
        Optional<Long> itemId = Optional.empty();
        assertThrows(RuntimeException.class, () -> itemServiceImp.update(id, userId, itemId));
    }

    @Test
    public void whenUpdate_thenPositive() {

        Item i = new Item();
        i.setOwner(1L);
        ItemDtoUpdate id = new ItemDtoUpdate();

        id.setDescription("q");
        id.setAvailable(true);
        id.setRequest(request);
        id.setName("q");
        Optional<Long> userId = Optional.of(1L);
        Optional<Long> itemId = Optional.of(1L);
        Mockito
                .when(itemStorage.findById(1L))
                .thenReturn(Optional.of(i));
        Mockito
                .when(userServiceImp.get(1L))
                .thenReturn(userDto);

        try (MockedStatic<MapperItemDto> mapper = Mockito.mockStatic(MapperItemDto.class)) {
            mapper.when(() -> MapperItemDto.toItem(itemDto)).thenReturn(item);
            mapper.when(() -> MapperItemDto.toItemDto(item)).thenReturn(itemDto);
            itemServiceImp.update(id, userId, itemId);
            Mockito.verify(itemStorage, Mockito.times(1)).save(i);
        }
    }

    @Test
    public void whenGetAndBadParam_thenNegative() {

        Long id = 1L;
        Optional<Long> userId = Optional.empty();

        Mockito
                .when(itemStorage.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(ItemNotFound.class, () -> itemServiceImp.get(id, userId, LocalDateTime.now()));
    }

    @Test
    public void whenGet_thenPositive() {

        Item i = new Item();
        i.setOwner(1L);
        Long id = 1L;
        Optional<Long> userId = Optional.of(1L);
        Booking booking = new Booking();
        Clock clock = Clock.fixed(Instant.parse("2014-12-23T10:15:30.00Z"), ZoneId.of("UTC"));
        List<Comment> comment = new ArrayList<>();
        Mockito
                .when(itemStorage.findById(1L))
                .thenReturn(Optional.of(i));
        Mockito
                .when(bookingStorage.findFirstByItem_idAndEndBookingBefore(1L, LocalDateTime.now(clock)))
                .thenReturn(Optional.of(booking));
        Mockito
                .when(bookingStorage.findFirstByItem_idAndStartAfter(1L, LocalDateTime.now(clock)))
                .thenReturn(Optional.of(booking));
        Mockito
                .when(commentStorage.getCommentByItemId(1L))
                .thenReturn(comment);
        try (MockedStatic<MapperItemDto> mapper = Mockito.mockStatic(MapperItemDto.class)) {
            mapper.when(() -> MapperItemDto.toItem(itemDto)).thenReturn(i);
            mapper.when(() -> MapperItemDto.toItemDto(i)).thenReturn(itemDto);
            itemServiceImp.get(id, userId, LocalDateTime.now(clock));
            Mockito.verify(itemStorage, Mockito.times(3)).findById(id);
            assertEquals(itemServiceImp.get(id, userId, LocalDateTime.now(clock)), MapperItemDto.toItemDto(i));
        }
    }


    @Test
    public void whenGetAllAndEmptyUserId_thePositive() {

        List<Item> l = new ArrayList<>();
        l.add(item);
        Optional<Long> userId = Optional.empty();
        Optional<Long> from = Optional.of(2L);
        Optional<Long> size = Optional.of(3L);
        Clock clock = Clock.fixed(Instant.parse("2014-12-23T10:15:30.00Z"), ZoneId.of("UTC"));
        Mockito
                .when(itemStorage.findAll())
                .thenReturn(l);
        try (MockedStatic<MapperItemDto> mapper = Mockito.mockStatic(MapperItemDto.class)) {
            mapper.when(() -> MapperItemDto.toItem(itemDto)).thenReturn(item);
            mapper.when(() -> MapperItemDto.toItemDto(item)).thenReturn(itemDto);
            itemServiceImp.getAllItemsUser(userId, from, size, LocalDateTime.now(clock));
            Mockito.verify(itemStorage, Mockito.times(1)).findAll();

        }
    }

    @Test
    public void whenGerAllAndTrueOwnerId_thenPositive() {

        item.setId(1L);
        itemDto.setId(1L);
        itemDto.setOwner(1L);
        item.setOwner(1L);
        List<Item> l = new ArrayList<>();
        l.add(item);
        Page<Item> t = new PageImpl<>(l);
        Optional<Long> userId = Optional.of(1L);
        Optional<Long> from = Optional.of(2L);
        Optional<Long> size = Optional.of(3L);
        Clock clock = Clock.fixed(Instant.parse("2014-12-23T10:15:30.00Z"), ZoneId.of("UTC"));
        Pageable pageable = PageRequest.of(from.get().intValue() / size.get().intValue(), size.get().intValue());
        List<Comment> comments = new ArrayList<>();
        Booking booking = new Booking();
        booking.setId(2L);
        Mockito
                .when(itemStorage.findById(1L))
                .thenReturn(Optional.of(item));
        Mockito
                .when(bookingStorage.findFirstByItem_idAndEndBookingBefore(1L, LocalDateTime.now(clock)))
                .thenReturn(Optional.of(booking));
        Mockito
                .when(bookingStorage.findFirstByItem_idAndStartAfter(1L, LocalDateTime.now(clock)))
                .thenReturn(Optional.of(booking));
        Mockito
                .when(itemStorage.findByOwner(1L, pageable))
                .thenReturn(t);
        try (MockedStatic<MapperItemDto> mapper = Mockito.mockStatic(MapperItemDto.class)) {
            mapper.when(() -> MapperItemDto.toItem(itemDto)).thenReturn(item);
            mapper.when(() -> MapperItemDto.toItemDto(item)).thenReturn(itemDto);
            itemServiceImp.getAllItemsUser(userId, from, size, LocalDateTime.now(clock));
            Mockito.verify(itemStorage, Mockito.times(1)).findByOwner(1L, pageable);


        }
    }

    @Test
    public void whenSearchAndEmptyText_thenException() {

        Optional<Long> from = Optional.of(2L);
        Optional<Long> size = Optional.of(3L);
        Optional<String> text = Optional.empty();
        assertThrows(RuntimeException.class, () -> itemServiceImp.search(text, from, size));
    }

    @Test
    public void whenSearchAndBlankText_thenException() {

        Optional<Long> from = Optional.of(2L);
        Optional<Long> size = Optional.of(3L);
        Optional<String> text = Optional.of("");
        assertEquals(itemServiceImp.search(text, from, size).size(), 0);
    }

    @Test
    public void whenSearch_thenPositive() {

        item.setId(1L);
        itemDto.setId(1L);
        Optional<Long> from = Optional.of(2L);
        Optional<Long> size = Optional.of(3L);
        Optional<String> text = Optional.of("gg");
        List<Item> l = new ArrayList<>();
        l.add(item);
        Page<Item> t = new PageImpl<>(l);
        Pageable pageable = PageRequest.of(from.get().intValue() / size.get().intValue(), size.get().intValue());
        try (MockedStatic<MapperItemDto> mapper = Mockito.mockStatic(MapperItemDto.class)) {
            mapper.when(() -> MapperItemDto.toItem(itemDto)).thenReturn(item);
            mapper.when(() -> MapperItemDto.toItemDto(item)).thenReturn(itemDto);
            Mockito
                    .when(itemStorage.findByDescriptionContainingIgnoreCaseAndAvailableTrue(text.get(), pageable))
                    .thenReturn(t);
            Mockito
                    .when(itemStorage.findByNameContainingIgnoreCaseAndAvailableTrue(text.get(), pageable))
                    .thenReturn(t);
            itemServiceImp.search(text, from, size);
            Mockito.verify(itemStorage, Mockito.times(1))
                    .findByDescriptionContainingIgnoreCaseAndAvailableTrue(text.get(), pageable);
            Mockito.verify(itemStorage, Mockito.times(1))
                    .findByNameContainingIgnoreCaseAndAvailableTrue(text.get(), pageable);

        }
    }


}