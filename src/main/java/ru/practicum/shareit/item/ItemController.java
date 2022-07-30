package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoUpdate;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto add(@Valid @RequestBody(required = false) Optional<ItemDto> itemDto
            , @RequestHeader("X-Sharer-User-Id") Optional<Long> userId) {
        return itemService.add(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDtoUpdate itemDtoUpdate
            , @RequestHeader("X-Sharer-User-Id") Optional<Long> userId,@PathVariable Optional<Long> itemId){
      return   itemService.update(itemDtoUpdate,userId,itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable Long itemId){
      return   itemService.get(itemId);
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId){
        return itemService.getAllItemsUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam Optional<String> text){
        return itemService.search(text);
    }
}
