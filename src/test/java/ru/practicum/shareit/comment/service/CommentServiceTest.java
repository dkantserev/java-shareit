package ru.practicum.shareit.comment.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.exception.BookingException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.comment.MapperComment;
import ru.practicum.shareit.comment.commentDto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.storage.CommentStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.UserExceptions.UserNotFound;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock
    CommentStorage storage;
    @Mock
    BookingStorage booking;
    @Mock
    UserStorage userStorage;
    @Mock
    ItemStorage itemStorage;
    @InjectMocks
    CommentService commentService;

    Comment comment = new Comment();
    Clock clock = Clock.fixed(Instant.parse("2014-12-23T10:15:30.00Z"), ZoneId.of("UTC"));
    Booking bookingModel = new Booking();
    User user = new User();
    Item item = new Item();
    CommentDto commentDto = CommentDto.builder().build();



    @Test
    public void whenAddWithEmptyParam_thenException(){
        java.util.Optional<Long> userId =Optional.empty();
        Optional<Long> itemId =Optional.empty();
        assertThrows(RuntimeException.class,()->commentService.add(userId,itemId,comment, LocalDateTime.now(clock)));

    }

    @Test
    public void whenAddAndEmptyBooking_thenException(){
        Optional<Long> userId =Optional.of(1L);
        Optional<Long> itemId =Optional.of(1L);
        List<Booking> bookingList = Collections.emptyList();
        Mockito
                .when(booking.checkForComment(1L,LocalDateTime.now(clock),1L))
                                .thenReturn(bookingList);
        assertThrows(BookingException.class,()->commentService.add(userId,itemId,comment, LocalDateTime.now(clock)));

    }

    @Test
    public void whenAdd_thenPositive(){
        Optional<Long> userId =Optional.of(1L);
        Optional<Long> itemId =Optional.of(1L);
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(bookingModel);
        comment.setText("rr");
        user.setName("tt");
        user.setId(1L);

        Mockito
                .when(booking.checkForComment(1L,LocalDateTime.now(clock),1L))
                .thenReturn(bookingList);
        Mockito
                .when(userStorage.findById(1L))
                        .thenReturn(Optional.ofNullable(user));
        Mockito
                .when(itemStorage.findById(1L))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(storage.save(comment))
                .thenReturn(comment);
        try(MockedStatic<MapperComment> mapper = Mockito.mockStatic(MapperComment.class)){
            mapper.when(()->MapperComment.commentDto(comment)).thenReturn(commentDto);
            mapper.when(()->MapperComment.toComment(commentDto)).thenReturn(comment);
            commentService.add(userId,itemId,comment,LocalDateTime.now(clock));
            Mockito.verify(storage,Mockito.times(1)).save(comment);
        }

    }

    @Test
    public void whenTextBlank_theException(){
        Optional<Long> userId =Optional.of(1L);
        Optional<Long> itemId =Optional.of(1L);
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(bookingModel);
        comment.setText(" ");
        user.setName("tt");
        user.setId(1L);

        Mockito
                .when(booking.checkForComment(1L,LocalDateTime.now(clock),1L))
                .thenReturn(bookingList);
        assertThrows(BookingException.class,()->commentService.add(userId,itemId,comment,LocalDateTime.now(clock)));
    }

    @Test
    public void whenTextUserNotFound_theException(){
        Optional<Long> userId =Optional.of(1L);
        Optional<Long> itemId =Optional.of(1L);
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(bookingModel);
        comment.setText(" f");
        user.setName("tt");
        user.setId(1L);

        Mockito
                .when(booking.checkForComment(1L,LocalDateTime.now(clock),1L))
                .thenReturn(bookingList);
        Mockito
                .when(userStorage.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(UserNotFound.class,()->commentService.add(userId,itemId,comment,LocalDateTime.now(clock)));
    }

    @Test
    public void whenTextItemNotFound_theException(){
        Optional<Long> userId =Optional.of(1L);
        Optional<Long> itemId =Optional.of(1L);
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(bookingModel);
        comment.setText(" f");
        user.setName("tt");
        user.setId(1L);

        Mockito
                .when(booking.checkForComment(1L,LocalDateTime.now(clock),1L))
                .thenReturn(bookingList);
        Mockito
                .when(userStorage.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemStorage.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(UserNotFound.class,()->commentService.add(userId,itemId,comment,LocalDateTime.now(clock)));
    }

}