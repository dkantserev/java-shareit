package ru.practicum.server.comment.service;

import org.springframework.stereotype.Service;
import ru.practicum.server.booking.exception.BookingException;
import ru.practicum.server.booking.storage.BookingStorage;
import ru.practicum.server.comment.MapperComment;
import ru.practicum.server.comment.commentDto.CommentDto;
import ru.practicum.server.comment.model.Comment;
import ru.practicum.server.comment.storage.CommentStorage;
import ru.practicum.server.item.storage.ItemStorage;
import ru.practicum.server.user.UserExceptions.UserNotFound;
import ru.practicum.server.user.storage.UserStorage;


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
