package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoUpdate;
import ru.practicum.shareit.item.service.ItemServiceInterface;


import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemServiceInterface itemService;

    public ItemController( ItemServiceInterface itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto add(@Valid @RequestBody(required = false) Optional<ItemDto> itemDto,
                       @RequestHeader("X-Sharer-User-Id") Optional<Long> userId) {
        log.info("add item " + itemDto + " user id" + userId);
        return itemService.add(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDtoUpdate itemDtoUpdate,
                          @RequestHeader("X-Sharer-User-Id") Optional<Long> userId, @PathVariable Optional<Long> itemId) {
        log.info("update item " + itemDtoUpdate + " owner id " + userId + " item id " + itemId);
        return itemService.update(itemDtoUpdate, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable Long itemId,@RequestHeader("X-Sharer-User-Id") Optional<Long> userId) {
        log.info("get item id " + itemId);
        return itemService.get(itemId,userId);
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId) {
        log.info("get all item user ");
        return itemService.getAllItemsUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam Optional<String> text) {
        text.ifPresent(s -> log.info("search " + s));
        return itemService.search(text);
    }
}
