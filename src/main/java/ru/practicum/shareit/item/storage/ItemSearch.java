package ru.practicum.shareit.item.storage;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemSearch {

    Page<Item> findByOwner(Long owner, Pageable pageable);

    Page<Item> findByNameContainingIgnoreCaseAndAvailableTrue(String text, Pageable pageable);

    Page<Item> findByDescriptionContainingIgnoreCaseAndAvailableTrue(String text,Pageable pageable);

}
