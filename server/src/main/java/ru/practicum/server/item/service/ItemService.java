package ru.practicum.server.item.service;

import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.dto.ItemDtoUpdate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ItemService {
    ItemDto add(Optional<ItemDto> itemDto, Optional<Long> userId);

    ItemDto update(ItemDtoUpdate itemDtoUpdate, Optional<Long> userId, Optional<Long> itemId);


    List<ItemDto> getAllItemsUser(Optional<Long> userId, Optional<Long> from, Optional<Long> size, LocalDateTime localDateTime);

    List<ItemDto> search(Optional<String> text, Optional<Long> from, Optional<Long> size);



    ItemDto get(Long itemId, Optional<Long> userId, LocalDateTime localDateTime);
}
