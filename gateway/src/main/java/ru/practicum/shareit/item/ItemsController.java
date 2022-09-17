package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.CommentClient;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemsDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;



@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemsController {

    private final ItemClient itemClient;
    private final CommentClient commentClient;

    @PostMapping
    public ResponseEntity<Object> add(@Valid @RequestBody(required = false) ItemsDto itemDto,
                                      @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("add item " + itemDto + " user id" + userId);
        return itemClient.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestBody ItemsDto itemDtoUpdate,
                                         @RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        log.info("update item " + itemDtoUpdate + " owner id " + userId + " item id " + itemId);
        return itemClient.update(itemDtoUpdate, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("get item id " + itemId);
        return itemClient.getItems(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") long from,
                                         @Positive @RequestParam(name = "size", defaultValue = "10") long size) {
        log.info("get all item user ");
        return itemClient.getAllItemsUser(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text, @RequestHeader("X-Sharer-User-Id") long userId,
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") long from,
                                         @Positive @RequestParam(name = "size", defaultValue = "10") long size) {
        log.info("search " + text);
        return itemClient.search(text, from, size, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long itemId, @RequestBody CommentDto comment) {
        log.info("addComment userID " + userId + " itemId " + itemId);
        return commentClient.add(userId, itemId, comment);
    }
}
