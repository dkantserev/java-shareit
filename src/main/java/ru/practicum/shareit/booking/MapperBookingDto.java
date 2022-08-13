package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;


public class MapperBookingDto {

    public static Booking toBooking(BookingDto dto){
        Booking booking = new Booking();
        booking.setId(dto.getId());
        booking.setStart(dto.getStart());
        booking.setEndBooking(dto.getEnd());
        booking.setItem(dto.getItem());
        booking.setBooker(dto.getBooker());
        booking.setStatus(dto.getStatus());
        return booking;
    }

    public static BookingDto toBooking(Booking dto){
        return BookingDto.builder()
                .id(dto.getId())
                .start(dto.getStart())
                .end(dto.getEndBooking())
                .item(dto.getItem())
                .booker(dto.getBooker())
                .status(dto.getStatus())
                .build();
    }


}
