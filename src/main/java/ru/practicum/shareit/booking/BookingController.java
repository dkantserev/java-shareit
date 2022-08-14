package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.StatusException;
import ru.practicum.shareit.booking.service.BookingServiceImp;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @GetMapping("/{bookingId}")
    public BookingDto get(@PathVariable Optional<Long> bookingId,@RequestHeader("X-Sharer-User-Id") Optional<Long> userId) {
        log.info("get item id " + bookingId);
        return bookingService.get(bookingId,userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto setApproved (@PathVariable Optional<Long> bookingId,@RequestParam Optional<String> approved
            ,@RequestHeader("X-Sharer-User-Id") Optional<Long> userId ){
        return bookingService.setApproved(bookingId,approved,userId);
    }

    @GetMapping
    public List<BookingDto> getAll(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,@RequestParam(defaultValue = "ALL") String state){
        return bookingService.getAll(userId,state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllOwner(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,@RequestParam(defaultValue = "ALL") String state){
        return bookingService.getAllOwner(userId,state);
    }

    @ExceptionHandler(StatusException.class)
    public ResponseEntity<Map<String,String>> handleIllegalUpdateObject(StatusException e){
        Map<String,String> error = new HashMap<>();
        error.put("error",e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
