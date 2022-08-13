package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;



public class MapperItemDto {

    public static ItemDto toItemDto(Item item) {

        return ItemDto.builder().id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .owner(item.getOwner())
                .request(item.getRequest())
                .available(item.getAvailable())
                .build();
    }

    public static Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setOwner(item.getOwner());
        item.setRequest(item.getRequest());
        item.setAvailable(itemDto.getAvailable());

        return item;
    }
}
