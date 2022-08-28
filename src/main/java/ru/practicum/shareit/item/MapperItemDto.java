package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;


public class MapperItemDto {

    public static ItemDto toItemDto(Item item) {

        ItemDto ret= ItemDto.builder().id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .owner(item.getOwner())
                .request(item.getRequest())
                .available(item.getAvailable())
                .comments(item.getComments())
                .build();
        if(item.getRequest()!=null){
            ret.setRequestId(item.getRequest().getId());
        }
        return ret;
    }

    public static Item toItem(ItemDto itemDto) {

        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setOwner(item.getOwner());
        item.setRequest(item.getRequest());
        item.setAvailable(itemDto.getAvailable());
        item.setComments(itemDto.getComments());

        return item;
    }
}
