package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
@Component
public interface ItemStorage {
    Item add(Item item);
    Item get(Long id);
    List<Item> getAll();
    void delete(Long id);
    Long getNextId();
}
