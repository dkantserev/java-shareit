package ru.practicum.shareit.booking.service;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.MapperBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.BookingException;
import ru.practicum.shareit.booking.exception.StatusException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.item.itemException.ItemNotFound;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.UserExceptions.UserNotFound;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingServiceImp implements BookingService {

    private final BookingStorage storage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    public BookingServiceImp(BookingStorage storage, UserStorage userStorage, ItemStorage itemStorage) {
        this.storage = storage;
        this.userStorage = userStorage;
        this.itemStorage = itemStorage;
    }

    @Override
    public BookingDto add(Optional<BookingDto> booking, Optional<Long> userId, LocalDateTime localDateTime) {
        if (booking.isPresent() && userId.isPresent()) {
            if (userStorage.findById(userId.get()).isEmpty()) {
                throw new UserNotFound("user not found");
            }
            if (itemStorage.findById(booking.get().getItemId()).isEmpty()) {
                throw new ItemNotFound("item not found");
            }
            if (booking.get().getStart() != null && booking.get().getStart() != null &&
                    booking.get().getStart().isBefore(localDateTime)) {
                throw new BookingException("time problem");
            }
            if (booking.get().getStart() != null && booking.get().getStart() != null &&
                    booking.get().getEnd().isBefore(localDateTime)) {
                throw new BookingException("time problem");
            }
            if (itemStorage.findById(booking.get().getItemId()).get().getOwner().equals(userId.get())) {
                throw new UserNotFound("you item owner");
            }

            if (itemStorage.findById(booking.get().getItemId()).isPresent() &&
                    itemStorage.findById(booking.get().getItemId()).get().getAvailable().equals(true)) {
                booking.get().setStatus(BookingStatus.WAITING);
                if (userStorage.findById(userId.get()).isPresent() &&
                        itemStorage.findById(booking.get().getItemId()).isPresent()) {
                    booking.get().setBooker(userStorage.findById(userId.get()).get());
                    booking.get().setItem(itemStorage.findById(booking.get().getItemId()).get());
                }
                Booking booking1 = MapperBookingDto.toBooking(booking.get());
                storage.save(booking1);
                return MapperBookingDto.toBookingDto(booking1);
            }
            throw new BookingException("item unavailabed");
        }
        throw new RuntimeException("bad query");
    }

    @Override
    public BookingDto get(Optional<Long> bookingId, Optional<Long> userId) {
        Booking booking1;

        if (bookingId.isPresent() && userId.isPresent()) {
            if (storage.findById(bookingId.get()).isPresent()) {
                booking1 = storage.findById(bookingId.get()).get();
                if (Objects.equals(booking1.getItem().getOwner(), userId.get()) ||
                        Objects.equals(booking1.getBooker().getId(), userId.get())) {
                    return MapperBookingDto.toBookingDto(booking1);
                } else {
                    throw new UserNotFound("id is not the owner or booker");
                }
            }
            throw new UserNotFound("booking not found");
        }
        throw new RuntimeException("bad query");
    }

    @Override
    public BookingDto setApproved(Optional<Long> bookingId, Optional<String> approved, Optional<Long> userId) {

        if (bookingId.isPresent() && approved.isPresent() && userId.isPresent()) {
            if (checkForSetApproved(bookingId.get(), approved.get(), userId.get())) {

                storage.findById(bookingId.get()).orElseThrow(() -> new UserNotFound("not owner"))
                        .setStatus(BookingStatus.APPROVED);
            } else {

                storage.findById(bookingId.get()).orElseThrow(() -> new UserNotFound("not owner"))
                        .setStatus(BookingStatus.REJECTED);
            }
            storage.flush();
            return MapperBookingDto.toBookingDto(storage.findById(bookingId.get())
                    .orElseThrow(() -> new UserNotFound("not owner")));
        }
        throw new RuntimeException("bad query");
    }


    @Override
    public List<BookingDto> getAll(Optional<Long> userId, String state, Optional<Long> from, Optional<Long> size) {

        Pageable pageable = PageRequest.of(0, 20);
        if (from.isPresent() && size.isPresent()) {
            if (from.get() < 0 || size.get() < 0) {
                throw new RuntimeException("negative param");
            }
            int start = from.get().intValue() / size.get().intValue();
            pageable = PageRequest.of(start, size.get().intValue(), Sort.by(Sort.Direction.DESC, "start"));
        }
        if (userId.isEmpty()) {
            throw new RuntimeException("need user id");
        }
        if (userStorage.findById(userId.get()).isEmpty()) {
            throw new UserNotFound("user not found");
        }
        List<BookingDto> all = new ArrayList<>();
        storage.findByBooker_id(userId.get(), pageable).getContent().forEach(o -> all.add(MapperBookingDto.toBookingDto(o)));
        if (state.equals("ALL")) {
            return all.stream().sorted(Comparator.comparing(BookingDto::getEnd).reversed()).collect(Collectors.toList());
        }
        if (state.equals("FUTURE")) {
            return all.stream().filter(o -> o.getStart().isAfter(LocalDateTime.now()))
                    .sorted(Comparator.comparing(BookingDto::getEnd).reversed()).collect(Collectors.toList());
        }
        if (state.equals("PAST")) {
            return all.stream().filter(o -> o.getEnd().isBefore(LocalDateTime.now()))
                    .sorted(Comparator.comparing(BookingDto::getEnd)).collect(Collectors.toList());
        }
        if (state.equals("WAITING")) {
            return all.stream().filter(o -> o.getStatus().equals(BookingStatus.WAITING))
                    .sorted(Comparator.comparing(BookingDto::getStart)).collect(Collectors.toList());
        }
        if (state.equals("CURRENT")) {
            return all.stream().filter(o -> o.getStatus().equals(BookingStatus.REJECTED))
                    .sorted(Comparator.comparing(BookingDto::getStart)).collect(Collectors.toList());
        }
        if (state.equals("REJECTED")) {
            return all.stream().filter(o -> o.getStatus().equals(BookingStatus.REJECTED))
                    .sorted(Comparator.comparing(BookingDto::getStart)).collect(Collectors.toList());
        }
        throw new StatusException("Unknown state: UNSUPPORTED_STATUS");

    }

    @Override
    public List<BookingDto> getAllOwner(Optional<Long> userId, String state, Optional<Long> from, Optional<Long> size, LocalDateTime localDateTime) {
        Pageable pageable = PageRequest.of(0, 20);
        List<BookingDto> userBooking = new ArrayList<>();
        if (from.isPresent() && size.isPresent()) {
            if (from.get() < 0 || size.get() < 0) {
                throw new RuntimeException("negative param");
            }
            int start = from.get().intValue() / size.get().intValue();
            pageable = PageRequest.of(start, size.get().intValue(), Sort.by(Sort.Direction.DESC, "start"));
        }

        if (userId.isEmpty()) {
            throw new RuntimeException("need user id");
        }
        if (userStorage.findById(userId.get()).isEmpty()) {
            throw new UserNotFound("user not found");
        }

        if (state.equals("ALL")) {
            storage.ownerBooking(userId.get(), pageable).getContent().forEach(o -> userBooking.add(MapperBookingDto.toBookingDto(o)));
            return userBooking;
        }
        if (state.equals("FUTURE")) {
            storage.ownerBookingFuture(userId.get(), localDateTime, pageable).getContent()
                    .forEach(o -> userBooking.add(MapperBookingDto.toBookingDto(o)));
            return userBooking;
        }
        if (state.equals("PAST")) {
            storage.ownerBookingPast(userId.get(), localDateTime, pageable).getContent()
                    .forEach(o -> userBooking.add(MapperBookingDto.toBookingDto(o)));
            return userBooking;
        }
        if (state.equals("WAITING")) {
            storage.ownerBookingWaiting(userId.get(), BookingStatus.WAITING,
                    localDateTime, pageable).getContent().forEach(o -> userBooking.add(MapperBookingDto.toBookingDto(o)));
            return userBooking;
        }
        if (state.equals("CURRENT")) {
            storage.ownerBookingWaiting(userId.get(), BookingStatus.REJECTED,
                    localDateTime, pageable).getContent().forEach(o -> userBooking.add(MapperBookingDto.toBookingDto(o)));
            return userBooking;
        }
        if (state.equals("REJECTED")) {
            storage.ownerBookingWaiting(userId.get(), BookingStatus.REJECTED,
                    localDateTime, pageable).getContent().forEach(o -> userBooking.add(MapperBookingDto.toBookingDto(o)));
            return userBooking;
        }
        throw new StatusException("Unknown state: UNSUPPORTED_STATUS");
    }

    private Boolean checkForSetApproved(Long bookingId, String approved, Long userId) {

        Boolean checkApproved = Boolean.parseBoolean(approved);
        if (storage.findById(bookingId).isEmpty()) {
            throw new RuntimeException("not found booking");
        }
        if (!Objects.equals(storage.findById(bookingId).get().getItem().getOwner(), userId)) {
            throw new UserNotFound("not owner");
        }
        if (checkApproved) {
            if (storage.findById(bookingId).get().getStatus().equals(BookingStatus.APPROVED)) {
                throw new BookingException("booking already confirmed");
            }
        }
        if (!checkApproved) {
            if (storage.findById(bookingId).get().getStatus().equals(BookingStatus.REJECTED)) {
                throw new BookingException("rejected");
            }
        }
        return checkApproved;
    }
}
