package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoUpdate;

import java.util.List;
import java.util.Optional;

public interface ItemServiceInterface {
    ItemDto add(Optional<ItemDto> itemDto, Optional<Long> userId);

    ItemDto update(ItemDtoUpdate itemDtoUpdate, Optional<Long> userId, Optional<Long> itemId);

    List<ItemDto> getAllItemsUser(Optional<Long> userId);

    List<ItemDto> search(Optional<String> text);

    ItemDto get(Long itemId);

}
