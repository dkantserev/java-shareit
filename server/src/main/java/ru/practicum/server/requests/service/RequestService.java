package ru.practicum.server.requests.service;

import ru.practicum.server.requests.dto.ItemRequestDto;

import java.util.List;
import java.util.Optional;

public interface RequestService {
    ItemRequestDto add(Optional<ItemRequestDto> request, Optional<Long> userId);

    List<ItemRequestDto> get(Optional<Long> userId);

    List<ItemRequestDto> getAll(Optional<Long> userId, Optional<Long> from, Optional<Long> size);

    ItemRequestDto getForId(Long requestId, Optional<Long> userId);
}
