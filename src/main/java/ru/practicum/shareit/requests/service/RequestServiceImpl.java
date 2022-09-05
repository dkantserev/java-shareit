package ru.practicum.shareit.requests.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.requests.MapperItemRequest;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.exception.RequestBadParams;
import ru.practicum.shareit.requests.exception.RequestNotFound;
import ru.practicum.shareit.requests.storage.RequestStorage;
import ru.practicum.shareit.user.UserExceptions.UserNotFound;
import ru.practicum.shareit.user.storage.UserStorage;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RequestServiceImpl implements RequestService {

    private final UserStorage userStorage;
    private final RequestStorage requestStorage;

    public RequestServiceImpl(UserStorage userStorage, RequestStorage requestStorage) {
        this.userStorage = userStorage;
        this.requestStorage = requestStorage;
    }

    @Override
    public ItemRequestDto add(Optional<ItemRequestDto> request, Optional<Long> userId) {
        if (request.isEmpty() || userId.isEmpty()) {
            throw new RequestBadParams("void body or id");
        }
        if (userStorage.findById(userId.get()).isEmpty()) {
            throw new UserNotFound("user not found");
        }
        request.get().setRequestor(userStorage.findById(userId.get()).get());
        request.get().setCreated(LocalDateTime.now());
        return MapperItemRequest.toDto(requestStorage.save(MapperItemRequest.toModel(request.get())));
    }

    @Override
    public List<ItemRequestDto> get(Optional<Long> userId) {
        if (userId.isEmpty()) {
            throw new RequestBadParams("void  id");
        }
        if (userStorage.findById(userId.get()).isEmpty()) {
            throw new UserNotFound("user not found");
        }
        List<ItemRequestDto> ret = new ArrayList<>();
        requestStorage.findByRequestor_idOrderByCreated(userId.get()).ifPresent(o -> ret.add(MapperItemRequest.toDto(o)));
        return ret;
    }

    @Override
    public List<ItemRequestDto> getAll(Optional<Long> userId, Optional<Long> from, Optional<Long> size) {
        List<ItemRequestDto> ret = new ArrayList<>();
        if (userId.isEmpty()) {
            throw new RequestBadParams("void  id");
        }
        if (from.isEmpty() && size.isEmpty()) {
            requestStorage.findByRequestor_idOrderByCreated(userId.get()).ifPresent(o -> ret.add(MapperItemRequest.toDto(o)));
            return ret;
        }
        if (from.isEmpty() || size.isEmpty()) {
            throw new RuntimeException("empty from or size");
        }
        if (from.get() < 0 || size.get() < 0) {
            throw new RuntimeException("negativ param");
        }
        int start = 0;
        if (from.get() > 0 && size.get() > 0) {
            start = size.get().intValue() / from.get().intValue();
        }
        requestStorage.findByRequestor_idNotOrderByCreatedDesc(userId.get(),
                        PageRequest.of(start, size.get().intValue()))
                .getContent().forEach(o -> ret.add(MapperItemRequest.toDto(o)));

        return ret;
    }

    @Override
    public ItemRequestDto getForId(Long requestId, Optional<Long> userId) {
        if (userId.isEmpty()) {
            throw new RequestBadParams("no user id");
        }
        if (userStorage.findById(userId.get()).isEmpty()) {
            throw new UserNotFound("user not found");
        }
        return MapperItemRequest.toDto(requestStorage.findById(requestId).orElseThrow(RequestNotFound::new));
    }
}
