package ru.practicum.shareit.requests;

import ru.practicum.shareit.item.MapperItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;


public class MapperItemRequest {

    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        List<ItemDto> items = new ArrayList<>();
        ItemRequestDto ret = ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestor(itemRequest.getRequestor())
                .created(itemRequest.getCreated()).build();
        if (itemRequest.getItems() != null && itemRequest.getItems().size() > 0) {
            itemRequest.getItems().forEach(o -> items.add(MapperItemDto.toItemDto(o)));
            ret.setItems(items);
            return ret;
        }
        ret.setItems(items);
        return ret;
    }

    public static ItemRequest toModel(ItemRequestDto dto) {
        ItemRequest request = new ItemRequest();
        List<Item> items = new ArrayList<>();
        request.setId(dto.getId());
        request.setDescription(dto.getDescription());
        request.setRequestor(dto.getRequestor());
        request.setCreated(dto.getCreated());
        if (dto.getItems() != null && dto.getItems().size() > 0) {
            dto.getItems().forEach(o -> items.add(MapperItemDto.toItem(o)));
            request.setItems(items);
        }
        request.setItems(items);
        return request;
    }

}
