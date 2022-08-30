package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.MapperBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.BookingException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.item.itemException.ItemNotFound;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.UserExceptions.UserNotFound;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImpTest {
    @Mock
    BookingStorage bookingStorage;
    @Mock
    UserStorage userStorage;
    @Mock
    ItemStorage itemStorage;

    @InjectMocks
    BookingServiceImp bookingServiceImp;

    BookingDto bookingDto = BookingDto.builder().build();
    Clock clock = Clock.fixed(Instant.parse("2014-12-23T10:15:30.00Z"), ZoneId.of("UTC"));
    LocalDateTime localDateTime = LocalDateTime.now(clock);
    User user = new User();
    Item item = new Item();
    Booking booking1 = new Booking();

    @Test
    public void whenAddAndEmptyParam_thenException() {
        Optional<BookingDto> booking = Optional.empty();
        Optional<Long> userId = Optional.empty();
        assertThrows(RuntimeException.class, () -> bookingServiceImp.add(booking, userId, localDateTime));
    }

    @Test
    public void whenAddAndUserNotFound_thenException() {
        Optional<BookingDto> booking = Optional.of(bookingDto);
        Optional<Long> userId = Optional.of(1L);
        Mockito
                .when(userStorage.findById(userId.get()))
                .thenReturn(Optional.empty());
        assertThrows(UserNotFound.class, () -> bookingServiceImp.add(booking, userId, localDateTime));
    }

    @Test
    public void whenAddItemNotFound_thenException() {
        Clock clock1 = Clock.fixed(Instant.parse("2011-12-23T10:15:30.00Z"), ZoneId.of("UTC"));
        bookingDto.setStart(LocalDateTime.now(clock1));
        bookingDto.setItemId(1L);
        Optional<BookingDto> booking = Optional.of(bookingDto);
        Optional<Long> userId = Optional.of(1L);
        Mockito
                .when(userStorage.findById(userId.get()))
                .thenReturn(Optional.ofNullable(user));
        Mockito
                .when(itemStorage.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ItemNotFound.class, () -> bookingServiceImp.add(booking, userId, localDateTime));
    }

    @Test
    public void whenAddAndStartInPast_thenException() {
        Clock clock1 = Clock.fixed(Instant.parse("2011-12-23T10:15:30.00Z"), ZoneId.of("UTC"));
        bookingDto.setStart(LocalDateTime.now(clock1));
        bookingDto.setItemId(1L);
        Optional<BookingDto> booking = Optional.of(bookingDto);
        Optional<Long> userId = Optional.of(1L);
        Mockito
                .when(itemStorage.findById(1L))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(userStorage.findById(userId.get()))
                .thenReturn(Optional.ofNullable(user));
        assertThrows(BookingException.class, () -> bookingServiceImp.add(booking, userId, localDateTime));
    }

    @Test
    public void whenAddAndStartInFuture_thenException() {
        Clock clock1 = Clock.fixed(Instant.parse("2015-11-23T10:15:30.00Z"), ZoneId.of("UTC"));
        Clock clock2 = Clock.fixed(Instant.parse("2011-12-23T10:15:30.00Z"), ZoneId.of("UTC"));
        bookingDto.setStart(LocalDateTime.now(clock1));
        bookingDto.setEnd(LocalDateTime.now(clock2));
        bookingDto.setItemId(1L);
        Optional<BookingDto> booking = Optional.of(bookingDto);
        Optional<Long> userId = Optional.of(1L);
        Mockito
                .when(itemStorage.findById(1L))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(userStorage.findById(userId.get()))
                .thenReturn(Optional.ofNullable(user));
        assertThrows(BookingException.class, () -> bookingServiceImp.add(booking, userId, localDateTime));
    }

    @Test
    public void whenAddUserIdItOwner_thenException() {
        Clock clock1 = Clock.fixed(Instant.parse("2015-11-23T10:15:30.00Z"), ZoneId.of("UTC"));
        Clock clock2 = Clock.fixed(Instant.parse("2015-12-23T10:15:30.00Z"), ZoneId.of("UTC"));
        item.setAvailable(true);
        item.setOwner(1L);
        bookingDto.setStart(LocalDateTime.now(clock1));
        bookingDto.setEnd(LocalDateTime.now(clock2));
        bookingDto.setItemId(1L);
        Optional<BookingDto> booking = Optional.of(bookingDto);
        Optional<Long> userId = Optional.of(1L);
        Mockito
                .when(itemStorage.findById(1L))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(userStorage.findById(userId.get()))
                .thenReturn(Optional.ofNullable(user));
        assertThrows(UserNotFound.class, () -> bookingServiceImp.add(booking, userId, localDateTime));

    }

    @Test
    public void whenAdd_thenPositive() {
        Clock clock1 = Clock.fixed(Instant.parse("2015-11-23T10:15:30.00Z"), ZoneId.of("UTC"));
        Clock clock2 = Clock.fixed(Instant.parse("2015-12-23T10:15:30.00Z"), ZoneId.of("UTC"));
        item.setAvailable(true);
        item.setOwner(2L);
        bookingDto.setStart(LocalDateTime.now(clock1));
        bookingDto.setEnd(LocalDateTime.now(clock2));
        bookingDto.setItemId(1L);
        Optional<BookingDto> booking = Optional.of(bookingDto);
        Optional<Long> userId = Optional.of(1L);
        Mockito
                .when(itemStorage.findById(1L))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(userStorage.findById(userId.get()))
                .thenReturn(Optional.ofNullable(user));
        try (MockedStatic<MapperBookingDto> mapper = Mockito.mockStatic(MapperBookingDto.class)) {
            mapper.when(() -> MapperBookingDto.toBooking(bookingDto)).thenReturn(booking1);
            mapper.when(() -> MapperBookingDto.toBookingDto(booking1)).thenReturn(bookingDto);
            bookingServiceImp.add(booking, userId, localDateTime);
            Mockito.verify(bookingStorage, Mockito.times(1)).save(booking1);
        }
    }

    @Test
    public void whenAddAndAvailableFalse_thenException() {
        Clock clock1 = Clock.fixed(Instant.parse("2015-11-23T10:15:30.00Z"), ZoneId.of("UTC"));
        Clock clock2 = Clock.fixed(Instant.parse("2015-12-23T10:15:30.00Z"), ZoneId.of("UTC"));
        item.setAvailable(false);
        item.setOwner(2L);
        bookingDto.setStart(LocalDateTime.now(clock1));
        bookingDto.setEnd(LocalDateTime.now(clock2));
        bookingDto.setItemId(1L);
        Optional<BookingDto> booking = Optional.of(bookingDto);
        Optional<Long> userId = Optional.of(1L);
        Mockito
                .when(itemStorage.findById(1L))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(userStorage.findById(userId.get()))
                .thenReturn(Optional.ofNullable(user));
        assertThrows(BookingException.class, () -> bookingServiceImp.add(booking, userId, localDateTime));
    }

    @Test
    public void whenGetAndBadParam_thenException() {
        Optional<Long> bookingId = Optional.empty();
        Optional<Long> userId = Optional.empty();
        assertThrows(RuntimeException.class, () -> bookingServiceImp.get(bookingId, userId));
    }

    @Test
    public void whenGetBookingNotFound_thenException() {
        Optional<Long> bookingId = Optional.of(1L);
        Optional<Long> userId = Optional.of(1L);
        Mockito
                .when(bookingStorage.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(UserNotFound.class, () -> bookingServiceImp.get(bookingId, userId));
    }

    @Test
    public void whenGetNoBookerAndNoOwner_thenException() {
        Optional<Long> bookingId = Optional.of(1L);
        Optional<Long> userId = Optional.of(1L);
        item.setOwner(1L);
        user.setId(1L);
        booking1.setBooker(user);
        booking1.setItem(item);
        Mockito
                .when(bookingStorage.findById(1L))
                .thenReturn(Optional.ofNullable(booking1));

        try (MockedStatic<MapperBookingDto> mapper = Mockito.mockStatic(MapperBookingDto.class)) {
            mapper.when(() -> MapperBookingDto.toBooking(bookingDto)).thenReturn(booking1);
            mapper.when(() -> MapperBookingDto.toBookingDto(booking1)).thenReturn(bookingDto);
            bookingServiceImp.get(bookingId,userId);
            Mockito.verify(bookingStorage, Mockito.times(2)).findById(1L);
        }
    }


}