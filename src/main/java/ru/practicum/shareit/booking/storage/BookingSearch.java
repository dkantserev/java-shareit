package ru.practicum.shareit.booking.storage;

import ru.practicum.shareit.booking.model.Booking;


import java.time.LocalDateTime;

import java.util.Optional;

public interface BookingSearch {

    Optional<Booking> findFirstByItem_idAndStartAfter(Long itemId, LocalDateTime dateTime);

    Optional<Booking> findFirstByItem_idAndEndBookingBefore(Long itemId, LocalDateTime dateTime);
}
