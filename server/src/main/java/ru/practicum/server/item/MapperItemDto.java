package ru.practicum.server.item;

import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.model.Item;


public class MapperItemDto {

    public static ItemDto toItemDto(Item item) {

        ItemDto ret = ItemDto.builder().id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .owner(item.getOwner())
                .request(item.getRequest())
                .available(item.getAvailable())
                .comments(item.getComments())
                .build();
        if (item.getRequest() != null) {
            ret.setRequestId(item.getRequest().getId());
        }
        return ret;
    }

    public static Item toItem(ItemDto itemDto) {

        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setOwner(itemDto.getOwner());
        item.setRequest(itemDto.getRequest());
        item.setAvailable(itemDto.getAvailable());
        item.setComments(itemDto.getComments());

        return item;
    }
}
