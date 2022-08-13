package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.MapperBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.BookingException;
import ru.practicum.shareit.booking.storage.BookingStorageInterface;
import ru.practicum.shareit.item.service.ItemServiceInterface;
import ru.practicum.shareit.user.UserExceptions.UserNotFound;
import ru.practicum.shareit.user.userSevice.UserServiceInterface;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BookingServiceImp {

    private final BookingStorageInterface storage;
    private final UserServiceInterface userService;
    private final ItemServiceInterface itemService;

    public BookingServiceImp(BookingStorageInterface storage, UserServiceInterface userService, ItemServiceInterface itemService) {
        this.storage = storage;
        this.userService = userService;
        this.itemService = itemService;
    }

    public BookingDto add(Optional<BookingDto> booking, Optional<Long> userId) {
        if (booking.isPresent() && userId.isPresent()) {
            if (userService.get(userId.get()) == null) {
                throw new UserNotFound("user not found");
            }
            if (booking.get().getStart() != null && booking.get().getStart() != null && booking.get().getStart().isBefore(LocalDateTime.now())) {
                throw new BookingException("time problem");
            }
            if (booking.get().getStart() != null && booking.get().getStart() != null && booking.get().getEnd().isBefore(LocalDateTime.now())) {
                throw new BookingException("time problem");
            }
            if (itemService.get(booking.get().getItemId()) != null && itemService.get(booking.get().getItemId()).getAvailable().equals(true)) {
                return MapperBookingDto.toBooking(storage.save(MapperBookingDto.toBooking(booking.get())));
            }
            throw new BookingException("item unavailabed");
        }
        throw new RuntimeException("bad query");
    }
}
