package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class MapperItemDto {

    public static ItemDto toItemDto(Item item){
        return ItemDto.builder().id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .owner(item.getOwner())
                .request(item.getRequest())
                .available(item.getAvailable())
                .build();
    }

    public static Item toItem (ItemDto itemDto){
        return  Item.builder().id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .owner(itemDto.getOwner())
                .request(itemDto.getRequest())
                .available(itemDto.getAvailable())
                .build();
    }
}
