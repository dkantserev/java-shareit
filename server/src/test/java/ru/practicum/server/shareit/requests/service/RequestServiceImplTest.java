package ru.practicum.server.shareit.requests.service;


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

import ru.practicum.server.requests.MapperItemRequest;
import ru.practicum.server.requests.dto.ItemRequestDto;
import ru.practicum.server.requests.exception.RequestBadParams;
import ru.practicum.server.requests.exception.RequestNotFound;
import ru.practicum.server.requests.model.ItemRequest;
import ru.practicum.server.requests.service.RequestServiceImpl;
import ru.practicum.server.requests.storage.RequestStorage;
import ru.practicum.server.user.UserExceptions.UserNotFound;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @Mock
    UserStorage userStorage;
    @Mock
    RequestStorage requestStorage;
    @InjectMocks
    RequestServiceImpl requestService;

    ItemRequestDto itemRequestDto = ItemRequestDto.builder().build();
    Optional<ItemRequestDto> request;
    Optional<ItemRequest> requests;
    Optional<Long> userId;
    User user = new User();
    ItemRequest itemRequest = new ItemRequest();


    @Test
    public void whenAddAndEmptyParam_thenException() {

        request = Optional.empty();
        userId = Optional.empty();
        assertThrows(RequestBadParams.class, () -> requestService.add(request, userId));
    }

    @Test
    public void whenAddAndUserNotFound_thenException() {

        request = Optional.ofNullable(itemRequestDto);
        userId = Optional.of(1L);
        Mockito
                .when(userStorage.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(UserNotFound.class, () -> requestService.add(request, userId));
    }

    @Test
    public void whenAdd_thenPositive() {

        request = Optional.ofNullable(itemRequestDto);
        userId = Optional.of(1L);
        Mockito
                .when(userStorage.findById(1L))
                .thenReturn(Optional.of(user));
        try (MockedStatic<MapperItemRequest> mapper = Mockito.mockStatic(MapperItemRequest.class)) {
            mapper.when(() -> MapperItemRequest.toDto(itemRequest)).thenReturn(itemRequestDto);
            mapper.when(() -> MapperItemRequest.toModel(itemRequestDto)).thenReturn(itemRequest);
            requestService.add(request, userId);
            Mockito.verify(requestStorage, Mockito.times(1)).save(itemRequest);
        }
    }

    @Test
    public void whenGetAndEmptyParam_thenException() {

        userId = Optional.empty();
        assertThrows(RequestBadParams.class, () -> requestService.get(userId));
    }

    @Test

    public void whenGetAndUserNotFound_thenException() {
        userId = Optional.of(1L);
        Mockito
                .when(userStorage.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(UserNotFound.class, () -> requestService.get(userId));
    }

    @Test
    public void whenGet_thenPositive() {

        userId = Optional.of(1L);
        Mockito
                .when(userStorage.findById(1L))
                .thenReturn(Optional.of(user));
        requestService.get(userId);
        Mockito.verify(requestStorage, Mockito.times(1)).findByRequestor_idOrderByCreated(1L);
    }

    @Test
    public void whenGetAllAndEmptyParam_thenException() {

        userId = Optional.empty();
        Optional<Long> from = Optional.empty();
        Optional<Long> size = Optional.empty();
        assertThrows(RequestBadParams.class, () -> requestService.getAll(userId, from, size));
    }

    @Test
    public void whenGetAllAndNegativeParam_thenException() {

        userId = Optional.empty();
        Optional<Long> from = Optional.of(-1L);
        Optional<Long> size = Optional.of(-3L);
        assertThrows(RuntimeException.class, () -> requestService.getAll(userId, from, size));
    }

    @Test
    public void whenGetAll_thenPositive() {

        userId = Optional.of(1L);
        Optional<Long> from = Optional.of(1L);
        Optional<Long> size = Optional.of(3L);
        List<ItemRequest> l = new ArrayList<>();
        l.add(itemRequest);
        Page<ItemRequest> t = new PageImpl<>(l);
        int start = size.orElseThrow(RuntimeException::new).intValue() / from.orElseThrow(RuntimeException::new).intValue();
        try (MockedStatic<MapperItemRequest> mapper = Mockito.mockStatic(MapperItemRequest.class)) {
            mapper.when(() -> MapperItemRequest.toDto(itemRequest)).thenReturn(itemRequestDto);
            mapper.when(() -> MapperItemRequest.toModel(itemRequestDto)).thenReturn(itemRequest);
            Mockito
                    .when(requestStorage.findByRequestor_idNotOrderByCreatedDesc(1L,
                            PageRequest.of(start, size.get().intValue())))
                    .thenReturn(t);

            requestService.getAll(userId, from, size);
            Mockito.verify(requestStorage, Mockito.times(1))
                    .findByRequestor_idNotOrderByCreatedDesc(1L, PageRequest.of(start, size.get().intValue()));
        }
    }

    @Test
    public void whenGetForIdWithEmptyParam_thenException() {

        userId = Optional.empty();
        Long requestId = 1L;
        assertThrows(RequestBadParams.class, () -> requestService.getForId(requestId, userId));
    }

    @Test
    public void whenGetForIdUserNotFound_thenException() {

        userId = Optional.of(1L);
        Long requestId = 1L;
        Mockito
                .when(userStorage.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(UserNotFound.class, () -> requestService.getForId(requestId, userId));
    }

    @Test
    public void whenGetForIdRequestNotFound_thenException() {

        userId = Optional.of(1L);
        Long requestId = 1L;
        Mockito
                .when(userStorage.findById(1L))
                .thenReturn(Optional.of(user));
        try (MockedStatic<MapperItemRequest> mapper = Mockito.mockStatic(MapperItemRequest.class)) {
            mapper.when(() -> MapperItemRequest.toDto(itemRequest)).thenReturn(itemRequestDto);
            mapper.when(() -> MapperItemRequest.toModel(itemRequestDto)).thenReturn(itemRequest);
            assertThrows(RequestNotFound.class, () -> requestService.getForId(requestId, userId));
        }
    }

    @Test
    public void whenGetForId_thenPositive() {

        userId = Optional.of(1L);
        Long requestId = 1L;
        requests = Optional.of(itemRequest);
        Mockito
                .when(userStorage.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito
                .when(requestStorage.findById(1L))
                .thenReturn(requests);
        try (MockedStatic<MapperItemRequest> mapper = Mockito.mockStatic(MapperItemRequest.class)) {
            mapper.when(() -> MapperItemRequest.toDto(itemRequest)).thenReturn(itemRequestDto);
            mapper.when(() -> MapperItemRequest.toModel(itemRequestDto)).thenReturn(itemRequest);
            requestService.getForId(requestId, userId);
            Mockito.verify(requestStorage, Mockito.times(1)).findById(1L);
        }
    }
}