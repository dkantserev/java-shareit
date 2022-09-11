package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;



import java.time.LocalDateTime;

import java.util.Optional;

public interface BookingSearch {

    Optional<Booking> findFirstByItem_idAndStartAfter(Long itemId, LocalDateTime dateTime);

    Optional<Booking> findFirstByItem_idAndEndBookingBefore(Long itemId, LocalDateTime dateTime);

    Page<Booking> findByBooker_id(Long userId, Pageable pageable);
}
