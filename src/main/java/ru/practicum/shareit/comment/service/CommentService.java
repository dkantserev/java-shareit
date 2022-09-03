package ru.practicum.shareit.comment.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.exception.BookingException;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.comment.MapperComment;
import ru.practicum.shareit.comment.commentDto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.UserExceptions.UserNotFound;
import ru.practicum.shareit.user.storage.UserStorage;


import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentStorage storage;
    private final BookingStorage booking;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    public CommentService(CommentStorage storage, BookingStorage booking, UserStorage userStorage, ItemStorage itemStorage) {
        this.storage = storage;
        this.booking = booking;
        this.userStorage = userStorage;
        this.itemStorage = itemStorage;
    }

    public CommentDto add(Optional<Long> userId, Optional<Long> itemId, Comment comment,LocalDateTime localDateTime) {

        if (userId.isEmpty() || itemId.isEmpty()) {
            throw new RuntimeException("bad query");
        }
        if (booking.checkForComment(userId.get(), localDateTime, itemId.get()).isEmpty()) {
            throw new BookingException("user did not take the item");
        }
        if (comment.getText().isBlank()) {
            throw new BookingException("void text");
        }
        if (userStorage.findById(userId.get()).isEmpty() || itemStorage.findById(itemId.get()).isEmpty()) {
            throw new UserNotFound("user not found or item");
        }
        comment.setCreated(localDateTime);
        comment.setAuthorName(userStorage.findById(userId.get()).get().getName());
        comment.setItem(itemStorage.findById(itemId.get()).get());
        return MapperComment.commentDto(storage.save(comment));
    }
}
