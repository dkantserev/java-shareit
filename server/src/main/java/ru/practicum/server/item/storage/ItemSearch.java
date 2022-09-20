package ru.practicum.server.item.storage;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.server.item.model.Item;



public interface ItemSearch {

    Page<Item> findByOwner(Long owner, Pageable pageable);

    Page<Item> findByNameContainingIgnoreCaseAndAvailableTrue(String text, Pageable pageable);

    Page<Item> findByDescriptionContainingIgnoreCaseAndAvailableTrue(String text,Pageable pageable);

}
