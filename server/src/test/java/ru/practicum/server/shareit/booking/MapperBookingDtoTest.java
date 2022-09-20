package ru.practicum.server.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.server.booking.BookingStatus;
import ru.practicum.server.booking.MapperBookingDto;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.dto.BookingDtoForItem;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MapperBookingDtoTest {

    BookingDto bookingDto = BookingDto.builder().build();
    Booking booking = new Booking();
    BookingDtoForItem bookingDtoForItem = BookingDtoForItem.builder().build();
    Item item = new Item();
    BookingStatus bookingStatus = BookingStatus.REJECTED;
    User user = new User();

    @Test
    void toBooking() {

        bookingDto.setId(1L);
        bookingDto.setItem(item);
        bookingDto.setStatus(bookingStatus);
        bookingDto.setBooker(user);
        bookingDto.setStart(LocalDateTime.of(2022, 1, 1, 1, 1));
        bookingDto.setEnd(LocalDateTime.of(2022, 1, 1, 1, 2));
        assertEquals(MapperBookingDto.toBooking(bookingDto).getId(), bookingDto.getId());
        assertEquals(MapperBookingDto.toBooking(bookingDto).getItem(), bookingDto.getItem());
        assertEquals(MapperBookingDto.toBooking(bookingDto).getStatus(), bookingDto.getStatus());
        assertEquals(MapperBookingDto.toBooking(bookingDto).getBooker(), bookingDto.getBooker());
        assertEquals(MapperBookingDto.toBooking(bookingDto).getStart(), bookingDto.getStart());
        assertEquals(MapperBookingDto.toBooking(bookingDto).getEndBooking(), bookingDto.getEnd());
    }

    @Test
    void testToBooking() {

        booking.setId(1L);
        booking.setItem(item);
        booking.setStatus(bookingStatus);
        booking.setBooker(user);
        booking.setStart(LocalDateTime.of(2022, 1, 1, 1, 1));
        booking.setEndBooking(LocalDateTime.of(2022, 1, 1, 1, 2));
        assertEquals(MapperBookingDto.toBookingDto(booking).getId(), booking.getId());
        assertEquals(MapperBookingDto.toBookingDto(booking).getItem(), booking.getItem());
        assertEquals(MapperBookingDto.toBookingDto(booking).getStatus(), booking.getStatus());
        assertEquals(MapperBookingDto.toBookingDto(booking).getBooker(), booking.getBooker());
        assertEquals(MapperBookingDto.toBookingDto(booking).getStart(), booking.getStart());
        assertEquals(MapperBookingDto.toBookingDto(booking).getEnd(), booking.getEndBooking());
    }

    @Test
    void toBookingItem() {

        booking.setId(2L);
        assertEquals(MapperBookingDto.toBookingItem(booking).getId(), 2L);
    }
}