package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.commentDto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoUpdate;
import ru.practicum.shareit.item.service.ItemService;


import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final CommentService commentService;

    public ItemController(ItemService itemService, CommentService commentService) {
        this.itemService = itemService;
        this.commentService = commentService;
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
    public ItemDto get(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Optional<Long> userId) {
        log.info("get item id " + itemId);
        return itemService.get(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId) {
        log.info("get all item user ");
        return itemService.getAllItemsUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam Optional<String> text) {
        text.ifPresent(s -> log.info("search " + s));
        log.info("search " + text.get());
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                 @PathVariable Optional<Long> itemId, @RequestBody Comment comment) {
        log.info("addComment userID " + userId.get() + " itemId " + itemId.get());
        return commentService.add(userId, itemId, comment);
    }
}
