package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingServiceImp;

import javax.validation.Valid;
import java.util.Optional;

/**
 * // TODO .
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

private final BookingServiceImp bookingService;

    public BookingController( BookingServiceImp bookingService) {
        this.bookingService = bookingService;

    }

    @PostMapping
    public BookingDto add(@Valid @RequestBody(required = false) Optional<BookingDto> booking,
                          @RequestHeader("X-Sharer-User-Id") Optional<Long> userId) {
        log.info("add item " + booking + " user id" + userId);
        return bookingService.add(booking,userId);
    }
}
