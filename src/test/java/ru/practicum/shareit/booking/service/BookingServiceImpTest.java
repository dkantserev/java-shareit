package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.MapperBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.BookingException;
import ru.practicum.shareit.booking.exception.StatusException;
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
import java.util.ArrayList;
import java.util.List;
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
            bookingServiceImp.get(bookingId, userId);
            Mockito.verify(bookingStorage, Mockito.times(2)).findById(1L);
        }
    }

    @Test
    public void whenSetApprovedWithEmptyParam_thenException() {

        Optional<Long> bookingId = Optional.empty();
        Optional<Long> userId = Optional.empty();
        Optional<String> approved = Optional.empty();
        assertThrows(RuntimeException.class, () -> bookingServiceImp.setApproved(bookingId, approved, userId));

    }

    @Test
    public void whenSetApprovedBookingFailCheck1_thenException() {

        Optional<Long> bookingId = Optional.of(1L);
        Optional<Long> userId = Optional.of(1L);
        Optional<String> approved = Optional.of("true");
        Mockito
                .when(bookingStorage.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> bookingServiceImp.setApproved(bookingId, approved, userId));

    }

    @Test
    public void whenSetApprovedBookingFailCheck2_thenException() {

        Optional<Long> bookingId = Optional.of(1L);
        Optional<Long> userId = Optional.of(1L);
        Optional<String> approved = Optional.of("true");
        item.setOwner(2L);
        user.setId(1L);
        booking1.setBooker(user);
        booking1.setItem(item);
        Mockito
                .when(bookingStorage.findById(1L))
                .thenReturn(Optional.of(booking1));
        assertThrows(UserNotFound.class, () -> bookingServiceImp.setApproved(bookingId, approved, userId));

    }

    @Test
    public void whenSetApprovedBookingFailCheck3_thenException() {

        Optional<Long> bookingId = Optional.of(1L);
        Optional<Long> userId = Optional.of(1L);
        Optional<String> approved = Optional.of("true");
        item.setOwner(1L);
        user.setId(1L);
        booking1.setStatus(BookingStatus.APPROVED);
        booking1.setBooker(user);
        booking1.setItem(item);
        Mockito
                .when(bookingStorage.findById(1L))
                .thenReturn(Optional.of(booking1));
        assertThrows(BookingException.class, () -> bookingServiceImp.setApproved(bookingId, approved, userId));

    }

    @Test
    public void whenSetApprovedBookingFailCheck4_thenException() {

        Optional<Long> bookingId = Optional.of(1L);
        Optional<Long> userId = Optional.of(1L);
        Optional<String> approved = Optional.of("false");
        item.setOwner(1L);
        user.setId(1L);
        booking1.setStatus(BookingStatus.REJECTED);
        booking1.setBooker(user);
        booking1.setItem(item);
        Mockito
                .when(bookingStorage.findById(1L))
                .thenReturn(Optional.of(booking1));
        assertThrows(BookingException.class, () -> bookingServiceImp.setApproved(bookingId, approved, userId));

    }

    @Test
    public void whenSetApproved_thenPositive1() {

        Optional<Long> bookingId = Optional.of(1L);
        Optional<Long> userId = Optional.of(1L);
        Optional<String> approved = Optional.of("true");
        item.setOwner(1L);
        user.setId(1L);
        booking1.setStatus(BookingStatus.WAITING);
        booking1.setBooker(user);
        booking1.setItem(item);
        Mockito
                .when(bookingStorage.findById(1L))
                .thenReturn(Optional.of(booking1));
        bookingServiceImp.setApproved(bookingId, approved, userId);
        assertEquals(booking1.getStatus(), BookingStatus.APPROVED);

    }

    @Test
    public void whenSetApproved_thenPositive2() {

        Optional<Long> bookingId = Optional.of(1L);
        Optional<Long> userId = Optional.of(1L);
        Optional<String> approved = Optional.of("false");
        item.setOwner(1L);
        user.setId(1L);
        booking1.setStatus(BookingStatus.WAITING);
        booking1.setBooker(user);
        booking1.setItem(item);
        Mockito
                .when(bookingStorage.findById(1L))
                .thenReturn(Optional.of(booking1));
        bookingServiceImp.setApproved(bookingId, approved, userId);
        assertEquals(booking1.getStatus(), BookingStatus.REJECTED);

    }

    @Test
    public void whenGetAllWithEmptyUserId_theException() {

        Optional<Long> userId = Optional.empty();
        String state = "ALL";
        Optional<Long> from = Optional.of(1L);
        Optional<Long> size = Optional.of(3L);
        assertThrows(RuntimeException.class, () -> bookingServiceImp.getAll(userId, state, from, size));
    }

    @Test
    public void whenGetUserNotFound_theException() {

        Optional<Long> userId = Optional.of(1L);
        String state = "ALL";
        Optional<Long> from = Optional.of(1L);
        Optional<Long> size = Optional.of(3L);
        Mockito
                .when(userStorage.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(UserNotFound.class, () -> bookingServiceImp.getAll(userId, state, from, size));
    }

    @Test
    public void whenGetAll_thePositive() {

        Optional<Long> userId = Optional.of(1L);
        String state = "ALL";
        Optional<Long> from = Optional.of(1L);
        Optional<Long> size = Optional.of(3L);
        List<Booking> l = new ArrayList<>();
        l.add(booking1);
        Page<Booking> t = new PageImpl<>(l);
        Pageable pageable = PageRequest.of(from.get().intValue() / size.get().intValue(),
                size.get().intValue(), Sort.by(Sort.Direction.DESC, "start"));
        Mockito
                .when(userStorage.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito
                .when(bookingStorage.findByBooker_id(1L, pageable))
                .thenReturn(t);
        try (MockedStatic<MapperBookingDto> mapper = Mockito.mockStatic(MapperBookingDto.class)) {
            mapper.when(() -> MapperBookingDto.toBooking(bookingDto)).thenReturn(booking1);
            mapper.when(() -> MapperBookingDto.toBookingDto(booking1)).thenReturn(bookingDto);
            bookingServiceImp.getAll(userId, state, from, size);
            Mockito.verify(bookingStorage, Mockito.times(1)).findByBooker_id(1L, pageable);
        }
    }

    @Test
    public void whenGetAllFuture_thePositive2() {

        Optional<Long> userId = Optional.of(1L);
        String state = "FUTURE";
        Optional<Long> from = Optional.of(1L);
        Optional<Long> size = Optional.of(3L);
        List<Booking> l = new ArrayList<>();
        l.add(booking1);
        Page<Booking> t = new PageImpl<>(l);
        booking1.setStart(LocalDateTime.now());
        booking1.setEndBooking(LocalDateTime.now().plusHours(3L));
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusHours(3L));
        Pageable pageable = PageRequest.of(from.get().intValue() / size.get().intValue(),
                size.get().intValue(), Sort.by(Sort.Direction.DESC, "start"));
        Mockito
                .when(userStorage.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito
                .when(bookingStorage.findByBooker_id(1L, pageable))
                .thenReturn(t);
        try (MockedStatic<MapperBookingDto> mapper = Mockito.mockStatic(MapperBookingDto.class)) {
            mapper.when(() -> MapperBookingDto.toBooking(bookingDto)).thenReturn(booking1);
            mapper.when(() -> MapperBookingDto.toBookingDto(booking1)).thenReturn(bookingDto);
            bookingServiceImp.getAll(userId, state, from, size);
            Mockito.verify(bookingStorage, Mockito.times(1)).findByBooker_id(1L, pageable);
        }
    }

    @Test
    public void whenGetAllPast_thePositive3() {

        Optional<Long> userId = Optional.of(1L);
        String state = "PAST";
        Optional<Long> from = Optional.of(1L);
        Optional<Long> size = Optional.of(3L);
        List<Booking> l = new ArrayList<>();
        l.add(booking1);
        Page<Booking> t = new PageImpl<>(l);
        booking1.setStart(LocalDateTime.now());
        booking1.setEndBooking(LocalDateTime.now().plusHours(3L));
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusHours(3L));
        Pageable pageable = PageRequest.of(from.get().intValue() / size.get().intValue(),
                size.get().intValue(), Sort.by(Sort.Direction.DESC, "start"));
        Mockito
                .when(userStorage.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito
                .when(bookingStorage.findByBooker_id(1L, pageable))
                .thenReturn(t);
        try (MockedStatic<MapperBookingDto> mapper = Mockito.mockStatic(MapperBookingDto.class)) {
            mapper.when(() -> MapperBookingDto.toBooking(bookingDto)).thenReturn(booking1);
            mapper.when(() -> MapperBookingDto.toBookingDto(booking1)).thenReturn(bookingDto);
            bookingServiceImp.getAll(userId, state, from, size);
            Mockito.verify(bookingStorage, Mockito.times(1)).findByBooker_id(1L, pageable);
        }
    }

    @Test
    public void whenGetAllWaiting_thePositive4() {

        Optional<Long> userId = Optional.of(1L);
        String state = "WAITING";
        Optional<Long> from = Optional.of(1L);
        Optional<Long> size = Optional.of(3L);
        List<Booking> l = new ArrayList<>();
        l.add(booking1);
        Page<Booking> t = new PageImpl<>(l);
        booking1.setStart(LocalDateTime.now());
        booking1.setEndBooking(LocalDateTime.now().plusHours(3L));
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusHours(3L));
        bookingDto.setStatus(BookingStatus.WAITING);
        Pageable pageable = PageRequest.of(from.get().intValue() / size.get().intValue(),
                size.get().intValue(), Sort.by(Sort.Direction.DESC, "start"));
        Mockito
                .when(userStorage.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito
                .when(bookingStorage.findByBooker_id(1L, pageable))
                .thenReturn(t);
        try (MockedStatic<MapperBookingDto> mapper = Mockito.mockStatic(MapperBookingDto.class)) {
            mapper.when(() -> MapperBookingDto.toBooking(bookingDto)).thenReturn(booking1);
            mapper.when(() -> MapperBookingDto.toBookingDto(booking1)).thenReturn(bookingDto);
            bookingServiceImp.getAll(userId, state, from, size);
            Mockito.verify(bookingStorage, Mockito.times(1)).findByBooker_id(1L, pageable);
        }
    }

    @Test
    public void whenGetAllCurrent_thePositive5() {

        Optional<Long> userId = Optional.of(1L);
        String state = "CURRENT";
        Optional<Long> from = Optional.of(1L);
        Optional<Long> size = Optional.of(3L);
        List<Booking> l = new ArrayList<>();
        l.add(booking1);
        Page<Booking> t = new PageImpl<>(l);
        booking1.setStart(LocalDateTime.now());
        booking1.setEndBooking(LocalDateTime.now().plusHours(3L));
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusHours(3L));
        bookingDto.setStatus(BookingStatus.REJECTED);
        Pageable pageable = PageRequest.of(from.get().intValue() / size.get().intValue(),
                size.get().intValue(), Sort.by(Sort.Direction.DESC, "start"));
        Mockito
                .when(userStorage.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito
                .when(bookingStorage.findByBooker_id(1L, pageable))
                .thenReturn(t);
        try (MockedStatic<MapperBookingDto> mapper = Mockito.mockStatic(MapperBookingDto.class)) {
            mapper.when(() -> MapperBookingDto.toBooking(bookingDto)).thenReturn(booking1);
            mapper.when(() -> MapperBookingDto.toBookingDto(booking1)).thenReturn(bookingDto);
            bookingServiceImp.getAll(userId, state, from, size);
            Mockito.verify(bookingStorage, Mockito.times(1)).findByBooker_id(1L, pageable);
        }
    }

    @Test
    public void whenGetAllRejected_thePositive5() {

        Optional<Long> userId = Optional.of(1L);
        String state = "REJECTED";
        Optional<Long> from = Optional.of(1L);
        Optional<Long> size = Optional.of(3L);
        List<Booking> l = new ArrayList<>();
        l.add(booking1);
        Page<Booking> t = new PageImpl<>(l);
        booking1.setStart(LocalDateTime.now());
        booking1.setEndBooking(LocalDateTime.now().plusHours(3L));
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusHours(3L));
        bookingDto.setStatus(BookingStatus.REJECTED);
        Pageable pageable = PageRequest.of(from.get().intValue() / size.get().intValue(),
                size.get().intValue(), Sort.by(Sort.Direction.DESC, "start"));
        Mockito
                .when(userStorage.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito
                .when(bookingStorage.findByBooker_id(1L, pageable))
                .thenReturn(t);
        try (MockedStatic<MapperBookingDto> mapper = Mockito.mockStatic(MapperBookingDto.class)) {
            mapper.when(() -> MapperBookingDto.toBooking(bookingDto)).thenReturn(booking1);
            mapper.when(() -> MapperBookingDto.toBookingDto(booking1)).thenReturn(bookingDto);
            bookingServiceImp.getAll(userId, state, from, size);
            Mockito.verify(bookingStorage, Mockito.times(1)).findByBooker_id(1L, pageable);
        }
    }

    @Test
    public void whenGetAllAndBadStatus_thePositive5() {

        Optional<Long> userId = Optional.of(1L);
        String state = "rWAITIrNGT";
        Optional<Long> from = Optional.of(1L);
        Optional<Long> size = Optional.of(3L);
        List<Booking> l = new ArrayList<>();
        l.add(booking1);
        Page<Booking> t = new PageImpl<>(l);
        booking1.setStart(LocalDateTime.now());
        booking1.setEndBooking(LocalDateTime.now().plusHours(3L));
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusHours(3L));
        bookingDto.setStatus(BookingStatus.REJECTED);
        Pageable pageable = PageRequest.of(from.get().intValue() / size.get().intValue(),
                size.get().intValue(), Sort.by(Sort.Direction.DESC, "start"));
        Mockito
                .when(userStorage.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito
                .when(bookingStorage.findByBooker_id(1L, pageable))
                .thenReturn(t);
        try (MockedStatic<MapperBookingDto> mapper = Mockito.mockStatic(MapperBookingDto.class)) {
            mapper.when(() -> MapperBookingDto.toBooking(bookingDto)).thenReturn(booking1);
            mapper.when(() -> MapperBookingDto.toBookingDto(booking1)).thenReturn(bookingDto);
            assertThrows(StatusException.class, () -> bookingServiceImp.getAll(userId, state, from, size));
        }
    }

    @Test
    public void whenGetAllOwnerWithEmptyUserId_WhenException() {

        Optional<Long> userId = Optional.empty();
        String state = "ALL";
        Optional<Long> from = Optional.of(1L);
        Optional<Long> size = Optional.of(3L);
        Clock clock = Clock.fixed(Instant.parse("2014-12-23T10:15:30.00Z"), ZoneId.of("UTC"));
        LocalDateTime localDateTime1 = LocalDateTime.now(clock);
        assertThrows(RuntimeException.class, () -> bookingServiceImp.getAllOwner(userId, state, from, size, localDateTime1));
    }

    @Test
    public void whenGetAllOwnerUserNotFound_theException() {

        Optional<Long> userId = Optional.of(1L);
        String state = "ALL";
        Optional<Long> from = Optional.of(1L);
        Optional<Long> size = Optional.of(3L);
        Mockito
                .when(userStorage.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(UserNotFound.class, () -> bookingServiceImp.getAllOwner(userId, state, from, size, localDateTime));
    }

    @Test
    public void whenGetAllOwnerAndBadStatus_theException() {

        Optional<Long> userId = Optional.of(1L);
        String state = "ALLOHA";
        Optional<Long> from = Optional.of(1L);
        Optional<Long> size = Optional.of(3L);
        Mockito
                .when(userStorage.findById(1L))
                .thenReturn(Optional.of(user));
        assertThrows(StatusException.class, () -> bookingServiceImp.getAllOwner(userId, state, from, size, localDateTime));
    }

    @Test
    public void whenGetAllOwnerAll_thePositive() {

        Optional<Long> userId = Optional.of(1L);
        String state = "ALL";
        Optional<Long> from = Optional.of(1L);
        Optional<Long> size = Optional.of(3L);
        List<Booking> l = new ArrayList<>();
        l.add(booking1);
        Page<Booking> t = new PageImpl<>(l);
        Pageable pageable = PageRequest.of(from.get().intValue() / size.get().intValue(),
                size.get().intValue(), Sort.by(Sort.Direction.DESC, "start"));
        Clock clock = Clock.fixed(Instant.parse("2014-12-23T10:15:30.00Z"), ZoneId.of("UTC"));
        LocalDateTime localDateTime1 = LocalDateTime.now(clock);
        Mockito
                .when(userStorage.findById(1L))
                .thenReturn(Optional.ofNullable(user));
        Mockito
                .when(bookingStorage.ownerBooking(1L, pageable))
                .thenReturn(t);
        try (MockedStatic<MapperBookingDto> mapper = Mockito.mockStatic(MapperBookingDto.class)) {
            mapper.when(() -> MapperBookingDto.toBooking(bookingDto)).thenReturn(booking1);
            mapper.when(() -> MapperBookingDto.toBookingDto(booking1)).thenReturn(bookingDto);
            bookingServiceImp.getAllOwner(userId, state, from, size, localDateTime1);
            Mockito.verify(bookingStorage, Mockito.times(1)).ownerBooking(1L, pageable);
        }
    }

    @Test
    public void whenGetAllOwnerFuture_thePositive() {

        Optional<Long> userId = Optional.of(1L);
        String state = "FUTURE";
        Optional<Long> from = Optional.of(1L);
        Optional<Long> size = Optional.of(3L);
        List<Booking> l = new ArrayList<>();
        l.add(booking1);
        Page<Booking> t = new PageImpl<>(l);
        Clock clock = Clock.fixed(Instant.parse("2014-12-23T10:15:30.00Z"), ZoneId.of("UTC"));
        LocalDateTime localDateTime1 = LocalDateTime.now(clock);

        Pageable pageable = PageRequest.of(from.get().intValue() / size.get().intValue(),
                size.get().intValue(), Sort.by(Sort.Direction.DESC, "start"));
        Mockito
                .when(userStorage.findById(1L))
                .thenReturn(Optional.ofNullable(user));
        Mockito
                .when(bookingStorage.ownerBookingFuture(1L, localDateTime1, pageable))
                .thenReturn(t);
        try (MockedStatic<MapperBookingDto> mapper = Mockito.mockStatic(MapperBookingDto.class)) {
            mapper.when(() -> MapperBookingDto.toBooking(bookingDto)).thenReturn(booking1);
            mapper.when(() -> MapperBookingDto.toBookingDto(booking1)).thenReturn(bookingDto);
            bookingServiceImp.getAllOwner(userId, state, from, size, localDateTime1);
            Mockito.verify(bookingStorage, Mockito.times(1)).ownerBookingFuture(1L, localDateTime1, pageable);
        }
    }

    @Test
    public void whenGetAllOwnerPast_thePositive() {

        Optional<Long> userId = Optional.of(1L);
        String state = "PAST";
        Optional<Long> from = Optional.of(1L);
        Optional<Long> size = Optional.of(3L);
        booking1.setStatus(BookingStatus.WAITING);
        List<Booking> l = new ArrayList<>();
        l.add(booking1);
        Page<Booking> t = new PageImpl<>(l);
        Clock clock = Clock.fixed(Instant.parse("2014-12-23T10:15:30.00Z"), ZoneId.of("UTC"));
        LocalDateTime localDateTime1 = LocalDateTime.now(clock);

        Pageable pageable = PageRequest.of(from.get().intValue() / size.get().intValue(),
                size.get().intValue(), Sort.by(Sort.Direction.DESC, "start"));
        Mockito
                .when(userStorage.findById(1L))
                .thenReturn(Optional.ofNullable(user));
        Mockito
                .when(bookingStorage.ownerBookingPast(1L, localDateTime1, pageable))
                .thenReturn(t);
        try (MockedStatic<MapperBookingDto> mapper = Mockito.mockStatic(MapperBookingDto.class)) {
            mapper.when(() -> MapperBookingDto.toBooking(bookingDto)).thenReturn(booking1);
            mapper.when(() -> MapperBookingDto.toBookingDto(booking1)).thenReturn(bookingDto);
            bookingServiceImp.getAllOwner(userId, state, from, size, localDateTime1);
            Mockito.verify(bookingStorage, Mockito.times(1)).ownerBookingPast(1L, localDateTime1, pageable);

        }
    }

    @Test
    public void whenGetAllOwnerWaiting_thePositive() {

        Optional<Long> userId = Optional.of(1L);
        String state = "WAITING";
        Optional<Long> from = Optional.of(1L);
        Optional<Long> size = Optional.of(3L);
        booking1.setStatus(BookingStatus.WAITING);
        List<Booking> l = new ArrayList<>();
        l.add(booking1);
        Page<Booking> t = new PageImpl<>(l);
        Clock clock = Clock.fixed(Instant.parse("2014-12-23T10:15:30.00Z"), ZoneId.of("UTC"));
        LocalDateTime localDateTime1 = LocalDateTime.now(clock);

        Pageable pageable = PageRequest.of(from.get().intValue() / size.get().intValue(),
                size.get().intValue(), Sort.by(Sort.Direction.DESC, "start"));
        Mockito
                .when(userStorage.findById(1L))
                .thenReturn(Optional.ofNullable(user));
        Mockito
                .when(bookingStorage.ownerBookingWaiting(1L, BookingStatus.WAITING, localDateTime1, pageable))
                .thenReturn(t);
        try (MockedStatic<MapperBookingDto> mapper = Mockito.mockStatic(MapperBookingDto.class)) {
            mapper.when(() -> MapperBookingDto.toBooking(bookingDto)).thenReturn(booking1);
            mapper.when(() -> MapperBookingDto.toBookingDto(booking1)).thenReturn(bookingDto);
            bookingServiceImp.getAllOwner(userId, state, from, size, localDateTime1);
            Mockito.verify(bookingStorage, Mockito.times(1)).ownerBookingWaiting(1L, BookingStatus.WAITING, localDateTime1, pageable);

        }
    }

    @Test
    public void whenGetAllOwnerCurrent_thePositive() {

        Optional<Long> userId = Optional.of(1L);
        String state = "CURRENT";
        Optional<Long> from = Optional.of(1L);
        Optional<Long> size = Optional.of(3L);
        booking1.setStatus(BookingStatus.WAITING);
        List<Booking> l = new ArrayList<>();
        l.add(booking1);
        Page<Booking> t = new PageImpl<>(l);
        Clock clock = Clock.fixed(Instant.parse("2014-12-23T10:15:30.00Z"), ZoneId.of("UTC"));
        LocalDateTime localDateTime1 = LocalDateTime.now(clock);

        Pageable pageable = PageRequest.of(from.get().intValue() / size.get().intValue(),
                size.get().intValue(), Sort.by(Sort.Direction.DESC, "start"));
        Mockito
                .when(userStorage.findById(1L))
                .thenReturn(Optional.ofNullable(user));
        Mockito
                .when(bookingStorage.ownerBookingWaiting(1L, BookingStatus.REJECTED, localDateTime1, pageable))
                .thenReturn(t);
        try (MockedStatic<MapperBookingDto> mapper = Mockito.mockStatic(MapperBookingDto.class)) {
            mapper.when(() -> MapperBookingDto.toBooking(bookingDto)).thenReturn(booking1);
            mapper.when(() -> MapperBookingDto.toBookingDto(booking1)).thenReturn(bookingDto);
            bookingServiceImp.getAllOwner(userId, state, from, size, localDateTime1);
            Mockito.verify(bookingStorage, Mockito.times(1)).ownerBookingWaiting(1L, BookingStatus.REJECTED, localDateTime1, pageable);

        }
    }

    @Test
    public void whenGetAllOwnerRejected_thePositive() {

        Optional<Long> userId = Optional.of(1L);
        String state = "REJECTED";
        Optional<Long> from = Optional.of(1L);
        Optional<Long> size = Optional.of(3L);
        booking1.setStatus(BookingStatus.WAITING);
        List<Booking> l = new ArrayList<>();
        l.add(booking1);
        Page<Booking> t = new PageImpl<>(l);
        Clock clock = Clock.fixed(Instant.parse("2014-12-23T10:15:30.00Z"), ZoneId.of("UTC"));
        LocalDateTime localDateTime1 = LocalDateTime.now(clock);

        Pageable pageable = PageRequest.of(from.get().intValue() / size.get().intValue(),
                size.get().intValue(), Sort.by(Sort.Direction.DESC, "start"));
        Mockito
                .when(userStorage.findById(1L))
                .thenReturn(Optional.ofNullable(user));
        Mockito
                .when(bookingStorage.ownerBookingWaiting(1L, BookingStatus.REJECTED, localDateTime1, pageable))
                .thenReturn(t);
        try (MockedStatic<MapperBookingDto> mapper = Mockito.mockStatic(MapperBookingDto.class)) {
            mapper.when(() -> MapperBookingDto.toBooking(bookingDto)).thenReturn(booking1);
            mapper.when(() -> MapperBookingDto.toBookingDto(booking1)).thenReturn(bookingDto);
            bookingServiceImp.getAllOwner(userId, state, from, size, localDateTime1);
            Mockito.verify(bookingStorage, Mockito.times(1)).ownerBookingWaiting(1L, BookingStatus.REJECTED, localDateTime1, pageable);

        }
    }
}