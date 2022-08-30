package ru.practicum.shareit.item.service;

import org.assertj.core.internal.bytebuddy.dynamic.DynamicType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.exception.BookingException;
import ru.practicum.shareit.booking.storage.BookingStorage;
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
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.userSevice.UserServiceImp;

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
    public void whenEmptyParam_whenException(){

        ItemDtoUpdate id = new ItemDtoUpdate();
        Optional<Long> userId = Optional.empty();
        Optional<Long> itemId =Optional.empty();
        assertThrows(RuntimeException.class,()->itemServiceImp.update(id,userId,itemId));
    }
    @Test
    public void whenUpdate_thenPositive() {
        Item i = new Item();
        i.setOwner(1L);
        ItemDtoUpdate id = new ItemDtoUpdate();
        Optional<Long> userId = Optional.of(1L);
        Optional<Long> itemId = Optional.of(1L);
        Mockito
                .when(itemStorage.findById(1L))
                .thenReturn(Optional.of(i));
        Mockito
                .when(userServiceImp.get(1L))
                .thenReturn(userDto);

        try(MockedStatic<MapperItemDto> mapper=Mockito.mockStatic(MapperItemDto.class)){
            mapper.when(()->MapperItemDto.toItem(itemDto)).thenReturn(item);
            mapper.when(()->MapperItemDto.toItemDto(item)).thenReturn(itemDto);
            itemServiceImp.update(id,userId,itemId);
            Mockito.verify(itemStorage,Mockito.times(1)).save(i);
        }
    }

    @Test
    public void whenGetAndBadParam_thenNegative(){
        Long id=1L;
        Optional<Long> userId=Optional.empty();
        Mockito
                .when(itemStorage.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(ItemNotFound.class,()->itemServiceImp.get(id,userId));
    }


}