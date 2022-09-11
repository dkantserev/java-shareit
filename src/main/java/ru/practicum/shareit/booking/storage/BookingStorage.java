package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;


import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingStorage extends JpaRepository<Booking, Long>, BookingSearch {

    @Query("select b from Booking b where  b.item.owner= ?1 order by b.endBooking desc ")
    public Page<Booking> ownerBooking(Long id, Pageable pageable);

    @Query("select b from Booking b where  b.item.owner= ?1 and b.start > ?2 order by b.endBooking desc ")
    public Page<Booking> ownerBookingFuture(Long id, LocalDateTime data, Pageable pageable);

    @Query("select b from Booking b where  b.item.owner= ?1 and b.endBooking < ?2 order by b.endBooking desc ")
    public Page<Booking> ownerBookingPast(Long id, LocalDateTime data, Pageable pageable);

    @Query("select b from Booking b where  b.item.owner= ?1 and b.status =?2 and b.endBooking>?3 order by b.endBooking desc ")
    public Page<Booking> ownerBookingWaiting(Long id, BookingStatus status, LocalDateTime now, Pageable pageable);

    @Query("select b from Booking  b where b.booker.id= ?1 and b.endBooking<?2 and b.item.id=?3")
    public List<Booking> checkForComment(Long userId, LocalDateTime now, Long itemId);

}
