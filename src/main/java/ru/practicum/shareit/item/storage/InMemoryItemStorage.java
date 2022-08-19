package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class InMemoryItemStorage implements ItemStorage {

    private List<Item> itemStorage = new ArrayList<>();
    private Long id = 0L;

    @Override
    public Item add(Item item) {
        itemStorage.add(item);
        return item;
    }

    @Override
    public Item get(Long id) {
        return itemStorage.stream().filter(o -> Objects.equals(o.getId(), id)).findFirst().orElse(null);
    }

    @Override
    public List<Item> getAll() {
        return itemStorage;
    }

    @Override
    public void delete(Long id) {
    }

    @Override
    public Long getNextId() {
        return ++id;
    }
}
