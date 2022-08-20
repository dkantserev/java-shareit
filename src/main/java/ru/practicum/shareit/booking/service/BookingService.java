package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    BookingDto add(Optional<BookingDto> booking, Optional<Long> userId);

    BookingDto get(Optional<Long> bookingId, Optional<Long> userId);

    BookingDto setApproved(Optional<Long> bookingId, Optional<String> approved, Optional<Long> userId);

    List<BookingDto> getAll(Optional<Long> userId, String state);

    List<BookingDto> getAllOwner(Optional<Long> userId, String state);
}
