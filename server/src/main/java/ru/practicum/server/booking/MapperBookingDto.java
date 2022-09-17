package ru.practicum.server.booking;

import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.dto.BookingDtoForItem;
import ru.practicum.server.booking.model.Booking;


public class MapperBookingDto {

    public static Booking toBooking(BookingDto dto) {
        Booking booking = new Booking();
        booking.setId(dto.getId());
        booking.setStart(dto.getStart());
        booking.setEndBooking(dto.getEnd());
        booking.setItem(dto.getItem());
        booking.setBooker(dto.getBooker());
        booking.setStatus(dto.getStatus());
        return booking;
    }

    public static BookingDto toBookingDto(Booking dto) {

        return BookingDto.builder()
                .id(dto.getId())
                .start(dto.getStart())
                .end(dto.getEndBooking())
                .item(dto.getItem())
                .booker(dto.getBooker())
                .status(dto.getStatus())
                .build();
    }

    public static BookingDtoForItem toBookingItem(Booking booking) {

        BookingDtoForItem bookingDtoForItem = BookingDtoForItem.builder().id(booking.getId()).build();
        if (booking.getBooker() != null) {
            bookingDtoForItem.setBookerId(booking.getBooker().getId());
        }
        return bookingDtoForItem;
    }
}
