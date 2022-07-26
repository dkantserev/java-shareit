package ru.practicum.server.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.item.dto.ItemDtoUpdate;
import ru.practicum.server.comment.commentDto.CommentDto;
import ru.practicum.server.comment.model.Comment;
import ru.practicum.server.comment.service.CommentService;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.service.ItemService;


import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
        LocalDateTime localDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        return itemService.get(itemId, userId, localDateTime);
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                @RequestParam(required = false) Optional<Long> from,
                                @RequestParam(required = false) Optional<Long> size) {
        log.info("get all item user ");
        LocalDateTime localDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        return itemService.getAllItemsUser(userId, from, size, localDateTime);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam Optional<String> text,
                                @RequestParam(required = false) Optional<Long> from,
                                @RequestParam(required = false) Optional<Long> size) {
        text.ifPresent(s -> log.info("search " + s));
        log.info("search " + text.orElseThrow(RuntimeException::new));
        return itemService.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                 @PathVariable Optional<Long> itemId, @RequestBody Comment comment) {
        log.info("addComment userID " + userId.orElse(-999L) + " itemId " + itemId.orElse(-999L));
        LocalDateTime localDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        return commentService.add(userId, itemId, comment, localDateTime);
    }
}
