package ru.practicum.server.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.server.item.MapperItemDto;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.requests.model.ItemRequest;

import static org.junit.Assert.assertEquals;

class MapperItemDtoTest {

    Item item = new Item();
    ItemDto itemDto = ItemDto.builder().build();

    @Test
    void toItemDto() {

        item.setId(1L);
        item.setRequest(new ItemRequest());
        item.setAvailable(true);
        item.setOwner(2L);
        item.setDescription("r");
        assertEquals(MapperItemDto.toItemDto(item).getId(), item.getId());
        assertEquals(MapperItemDto.toItemDto(item).getRequest(), item.getRequest());
        assertEquals(MapperItemDto.toItemDto(item).getAvailable(), item.getAvailable());
        assertEquals(MapperItemDto.toItemDto(item).getOwner(), item.getOwner());
        assertEquals(MapperItemDto.toItemDto(item).getDescription(), item.getDescription());
    }

    @Test
    void toItem() {

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemDto.setId(1L);
        itemDto.setRequest(itemRequest);
        itemDto.setAvailable(true);
        itemDto.setOwner(2L);
        itemDto.setDescription("r");
        assertEquals(MapperItemDto.toItem(itemDto).getId(), itemDto.getId());
        assertEquals(MapperItemDto.toItem(itemDto).getRequest().getId(), itemDto.getRequest().getId());
        assertEquals(MapperItemDto.toItem(itemDto).getAvailable(), itemDto.getAvailable());
        assertEquals(MapperItemDto.toItem(itemDto).getOwner(), itemDto.getOwner());
        assertEquals(MapperItemDto.toItem(itemDto).getDescription(), itemDto.getDescription());
    }
}