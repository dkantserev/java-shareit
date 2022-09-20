package ru.practicum.server.item.dto;

import lombok.Data;


@Data
public class ItemDtoUpdate {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner;
    private Long request;
}
