package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.StatusException;
import ru.practicum.shareit.booking.service.BookingService;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;

    }

    @PostMapping
    public BookingDto add(@RequestBody(required = false) Optional<BookingDto> booking,
                          @RequestHeader("X-Sharer-User-Id") Optional<Long> userId) {
        log.info("add item " + booking + " user id" + userId);
        LocalDateTime localDateTime =LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        return bookingService.add(booking, userId,localDateTime);
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(@PathVariable Optional<Long> bookingId, @RequestHeader("X-Sharer-User-Id") Optional<Long> userId) {
        log.info("get item id " + bookingId);
        return bookingService.get(bookingId, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto setApproved(@PathVariable Optional<Long> bookingId, @RequestParam Optional<String> approved,
                                  @RequestHeader("X-Sharer-User-Id") Optional<Long> userId) {
        log.info("patch " + bookingId);
        return bookingService.setApproved(bookingId, approved, userId);
    }

    @GetMapping
    public List<BookingDto> getAll(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                   @RequestParam(defaultValue = "ALL") String state,
                                   @RequestParam(required = false) Optional<Long> from,
                                   @RequestParam(required = false) Optional<Long> size) {
        log.info("get all booking userId " + userId);
        return bookingService.getAll(userId, state,from,size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllOwner(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                        @RequestParam(defaultValue = "ALL") String state,
                                        @RequestParam(required = false) Optional<Long> from,
                                        @RequestParam(required = false) Optional<Long> size) {
        log.info("get all booking ownerId " + userId);
        LocalDateTime localDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        return bookingService.getAllOwner(userId, state,from,size,localDateTime);
    }


    @ExceptionHandler(StatusException.class)
    public ResponseEntity<Map<String, String>> handleIllegalUpdateObject(StatusException e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
