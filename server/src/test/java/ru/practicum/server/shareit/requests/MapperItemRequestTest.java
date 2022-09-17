package ru.practicum.server.shareit.requests;


import org.junit.jupiter.api.Test;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.requests.MapperItemRequest;
import ru.practicum.server.requests.dto.ItemRequestDto;
import ru.practicum.server.requests.model.ItemRequest;
import ru.practicum.server.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

class MapperItemRequestTest {

    Long id = 1L;
    String description = "fff";
    User requestor = new User();
    LocalDateTime created = LocalDateTime.of(1111, 1, 1,
            1, 1, 1, 1);
    ItemDto itemDto = ItemDto.builder().build();
    Item item = new Item();


    @Test
    void toDto() {

        List<Item> items = new ArrayList<>();
        items.add(item);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setCreated(created);
        itemRequest.setDescription(description);
        itemRequest.setRequestor(requestor);
        itemRequest.setItems(items);
        ItemRequestDto test = MapperItemRequest.toDto(itemRequest);
        assertEquals(test.getId(), id);
        assertEquals(test.getDescription(), description);
        assertEquals(test.getCreated(), created);
        assertEquals(test.getItems().size(), items.size());

    }

    @Test
    void toModel() {

        List<ItemDto> itemsDto = new ArrayList<>();
        itemsDto.add(itemDto);
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().build();
        itemRequestDto.setId(1L);
        itemRequestDto.setCreated(created);
        itemRequestDto.setDescription(description);
        itemRequestDto.setRequestor(requestor);
        itemRequestDto.setItems(itemsDto);
        ItemRequest test = MapperItemRequest.toModel(itemRequestDto);
        assertEquals(test.getId(), id);
        assertEquals(test.getDescription(), description);
        assertEquals(test.getCreated(), created);
        assertEquals(test.getItems().size(), itemsDto.size());
    }
}