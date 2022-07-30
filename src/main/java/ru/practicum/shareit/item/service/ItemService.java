package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.MapperItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoUpdate;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.item.itemException.ItemNotFound;
import ru.practicum.shareit.user.UserExceptions.UserNotFound;
import ru.practicum.shareit.user.userSevice.UserService;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    public ItemService(ItemStorage itemStorage, UserService userService) {
        this.itemStorage = itemStorage;
        this.userService = userService;
    }

    public ItemDto add(Optional<ItemDto> itemDto, Optional<Long> userId) {
        if (userId.isEmpty()) {
            throw new RuntimeException("must be id");
        }
        if (checkUser(userId.get())) {
            itemDto.get().setOwner(userId.get());
        } else {
            throw new UserNotFound("user not found");
        }
        itemDto.get().setId(itemStorage.getNextId());
        itemStorage.add(MapperItemDto.toItem(itemDto.get()));
        return itemDto.get();
    }

    public ItemDto update(ItemDtoUpdate itemDtoUpdate, Optional<Long> userId, Optional<Long> itemId) {
        if (userId.isEmpty() || itemId.isEmpty()) {
            throw new RuntimeException("must be id");
        }
        if (checkUser(userId.get()) && Objects.equals(itemStorage.get(itemId.get()).getOwner(),
                userId.get())) {
            if (itemDtoUpdate.getName() != null) {
                itemStorage.get(itemId.get()).setName(itemDtoUpdate.getName());
            }
            if (itemDtoUpdate.getDescription() != null) {
                itemStorage.get(itemId.get()).setDescription(itemDtoUpdate.getDescription());
            }
            if (itemDtoUpdate.getAvailable() != null) {
                itemStorage.get(itemId.get()).setAvailable(itemDtoUpdate.getAvailable());
            }
            return MapperItemDto.toItemDto(itemStorage.get(itemId.get()));
        } else {
            throw new UserNotFound("user not found");
        }
    }

    public List<ItemDto> getAllItemsUser(Optional<Long> userId) {
        List<ItemDto> all = new ArrayList<>();
        itemStorage.getAll().forEach(o -> all.add(MapperItemDto.toItemDto(o)));
        if (userId.isPresent()) {
            return all.stream().filter(o -> Objects.equals(o.getOwner(), userId.get())).collect(Collectors.toList());
        }
        return all;
    }

    public List<ItemDto> search(Optional<String> text) {
        List<ItemDto> returnSearch = new ArrayList<>();
        if (text.isPresent()) {
            if (text.get().isBlank()) {
                return returnSearch;
            }
            String search = text.get().toLowerCase(Locale.ROOT);
            List<Item> searchAll = new ArrayList<>(itemStorage.getAll());
            List<Item> searchName = searchAll.stream().filter(o -> o.getName().toLowerCase().contains(search)).filter(Item::getAvailable).collect(Collectors.toList());
            searchName.addAll(searchAll.stream().filter(o -> o.getDescription().toLowerCase().contains(search)).filter(Item::getAvailable).collect(Collectors.toList()));
            List<Item> allFinal = searchName.stream().distinct().collect(Collectors.toList());
            allFinal.forEach(o -> returnSearch.add(MapperItemDto.toItemDto(itemStorage.get(o.getId()))));
            return returnSearch;
        }
        throw new ItemNotFound("empty param");
    }

    private Boolean checkUser(Long id) {
        return userService.get(id) != null;
    }

    public ItemDto get(Long itemId) {
        if (itemStorage.get(itemId) != null) {
            return MapperItemDto.toItemDto(itemStorage.get(itemId));
        } else {
            throw new ItemNotFound("item not found");
        }
    }


}
