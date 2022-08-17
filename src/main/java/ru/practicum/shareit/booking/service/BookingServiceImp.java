package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.MapperBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.BookingException;
import ru.practicum.shareit.booking.exception.StatusException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingStorageInterface;
import ru.practicum.shareit.item.MapperItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.itemException.ItemNotFound;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceInterface;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.MapperUserDto;
import ru.practicum.shareit.user.UserExceptions.UserNotFound;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.userSevice.UserServiceInterface;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingServiceImp {

    private final BookingStorageInterface storage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    public BookingServiceImp(BookingStorageInterface storage, UserServiceInterface userService, ItemServiceInterface itemService, UserStorage userStorage, ItemStorage itemStorage) {
        this.storage = storage;
        this.userStorage = userStorage;
        this.itemStorage = itemStorage;
    }

    public BookingDto add(Optional<BookingDto> booking, Optional<Long> userId) {
        if (booking.isPresent() && userId.isPresent()) {
            if (userStorage.findById(userId.get()).isEmpty()) {
                throw new UserNotFound("user not found");
            }
            if (itemStorage.findById(booking.get().getItemId()).isEmpty()) {
                throw new ItemNotFound("item not found");
            }
            if (booking.get().getStart() != null && booking.get().getStart() != null && booking.get().getStart().isBefore(LocalDateTime.now())) {
                throw new BookingException("time problem");
            }
            if (booking.get().getStart() != null && booking.get().getStart() != null && booking.get().getEnd().isBefore(LocalDateTime.now())) {
                throw new BookingException("time problem");
            }
            if(itemStorage.findById(booking.get().getItemId()).get().getOwner().equals(userId.get())){
                throw new UserNotFound("you item owner");
            }

            if (itemStorage.findById(booking.get().getItemId()).isPresent() && itemStorage.findById(booking.get().getItemId()).get().getAvailable().equals(true)) {
                booking.get().setStatus(BookingStatus.WAITING);
                if (userStorage.findById(userId.get()).isPresent() && itemStorage.findById(booking.get().getItemId()).isPresent()) {
                    booking.get().setBooker(userStorage.findById(userId.get()).get());
                    booking.get().setItem(itemStorage.findById(booking.get().getItemId()).get());
                }
                Booking booking1 = MapperBookingDto.toBooking(booking.get());
                storage.save(booking1);
                return MapperBookingDto.toBooking(booking1);
            }
            throw new BookingException("item unavailabed");
        }
        throw new RuntimeException("bad query");
    }

    public BookingDto get(Optional<Long> bookingId, Optional<Long> userId) {
        Booking booking1 = new Booking();

        if (bookingId.isPresent() && userId.isPresent()) {
            if (storage.findById(bookingId.get()).isPresent()) {
                booking1 = storage.findById(bookingId.get()).get();
                if (Objects.equals(booking1.getItem().getOwner(), userId.get()) || Objects.equals(booking1.getBooker().getId(), userId.get())) {
                    return MapperBookingDto.toBooking(booking1);
                } else {
                    throw new UserNotFound("id is not the owner or booker");
                }
            }
            throw new UserNotFound("user not found");
        }
        throw new RuntimeException("bad query");
    }

    public BookingDto setApproved(Optional<Long> bookingId, Optional<String> approved, Optional<Long> userId) {
        if (bookingId.isPresent() && approved.isPresent() && userId.isPresent()) {
            if (storage.findById(bookingId.get()).isPresent()) {
                if (Objects.equals(storage.findById(bookingId.get()).get().getItem().getOwner(), userId.get())) {
                    if (approved.get().equals("true")) {
                        if(storage.findById(bookingId.get()).get().getStatus().equals(BookingStatus.APPROVED)){
                            throw new BookingException("booking already confirmed");
                        }
                        storage.findById(bookingId.get()).get().setStatus(BookingStatus.APPROVED);
                    }
                    if (approved.get().equals("false")) {
                        if(storage.findById(bookingId.get()).get().getStatus().equals(BookingStatus.REJECTED)){
                            throw new BookingException("rejected");
                        }
                        storage.findById(bookingId.get()).get().setStatus(BookingStatus.REJECTED);
                    }
                    storage.flush();
                    return MapperBookingDto.toBooking(storage.findById(bookingId.get()).get());
                } else {
                    throw new UserNotFound("not owner");
                }
            } else {
                throw new RuntimeException("not found booking");
            }
        }
        throw new RuntimeException("bad query");
    }

    public List<BookingDto> getAll(Optional<Long> userId, String state) {

        if (userId.isEmpty()) {
            throw new RuntimeException("need user id");
        }
        if (userStorage.findById(userId.get()).isEmpty()) {
            throw new UserNotFound("user not found");
        }
        List<BookingDto> all = new ArrayList<>();
        userStorage.findById(userId.get()).get().getBooking().forEach(o -> all.add(MapperBookingDto.toBooking(o)));
        if (state.equals("ALL")) {
            return all.stream().sorted(Comparator.comparing(BookingDto::getEnd).reversed()).collect(Collectors.toList());
        }
        if (state.equals("FUTURE")) {
            return all.stream().filter(o -> o.getStart().isAfter(LocalDateTime.now())).sorted(Comparator.comparing(BookingDto::getEnd).reversed()).collect(Collectors.toList());
        }
        if (state.equals("PAST")) {
            return all.stream().filter(o -> o.getEnd().isBefore(LocalDateTime.now())).sorted(Comparator.comparing(BookingDto::getEnd)).collect(Collectors.toList());
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

    public List<BookingDto> getAllOwner(Optional<Long> userId, String state) {

        List<BookingDto> userBooking = new ArrayList<>();

        if (userId.isEmpty()) {
            throw new RuntimeException("need user id");
        }
        if (userStorage.findById(userId.get()).isEmpty()) {
            throw new UserNotFound("user not found");
        }

        if (state.equals("ALL")) {
            storage.ownerBooking(userId.get()).forEach(o -> userBooking.add(MapperBookingDto.toBooking(o)));
            return userBooking;
        }
        if (state.equals("FUTURE")) {
            storage.ownerBookingFuture(userId.get(),LocalDateTime.now()).forEach(o -> userBooking.add(MapperBookingDto.toBooking(o)));
            return userBooking;
        }
        if (state.equals("PAST")) {
            storage.ownerBookingPast(userId.get(),LocalDateTime.now()).forEach(o -> userBooking.add(MapperBookingDto.toBooking(o)));
            return userBooking;
        }
        if (state.equals("WAITING")) {
            storage.ownerBookingWaiting(userId.get(),BookingStatus.WAITING,LocalDateTime.now()).forEach(o -> userBooking.add(MapperBookingDto.toBooking(o)));
            return userBooking;
        }
        if (state.equals("CURRENT")) {
            storage.ownerBookingWaiting(userId.get(),BookingStatus.REJECTED,LocalDateTime.now()).forEach(o -> userBooking.add(MapperBookingDto.toBooking(o)));
            return userBooking;
        }
        if (state.equals("REJECTED")) {
            storage.ownerBookingWaiting(userId.get(),BookingStatus.REJECTED,LocalDateTime.now()).forEach(o -> userBooking.add(MapperBookingDto.toBooking(o)));
            return userBooking;
        }
        throw new StatusException("Unknown state: UNSUPPORTED_STATUS");
    }
    }
