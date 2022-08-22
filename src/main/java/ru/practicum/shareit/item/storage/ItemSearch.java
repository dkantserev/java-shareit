package ru.practicum.shareit.item.storage;


import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemSearch {

    List<Item> findByOwner(Long owner);

    List<Item> findByNameContainingIgnoreCaseAndAvailableTrue(String text);

    List<Item> findByDescriptionContainingIgnoreCaseAndAvailableTrue(String text);

}
