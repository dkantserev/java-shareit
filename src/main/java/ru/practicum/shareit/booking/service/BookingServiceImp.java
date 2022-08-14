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
           /*if (storage.findByItem_id(booking.get().getItemId())!=null) {
                if (storage.findByItem_id(booking.get().getItemId()).getStatus().equals(BookingStatus.APPROVED)) {
                    storage.findByItem_id(booking.get().getItemId()).getItem().setAvailable(false);
                    Booking booking1 = storage.findByItem_id(booking.get().getItemId());
                    storage.deleteById(storage.findByItem_id(booking.get().getItemId()).getId());
                    return MapperBookingDto.toBooking(storage.save(booking1));
                }
            }*/

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
                    throw new RuntimeException("wrong user");
                }
            }
        }
        throw new RuntimeException("bad query");
    }

    public BookingDto setApproved(Optional<Long> bookingId, Optional<String> approved, Optional<Long> userId) {
        if (bookingId.isPresent() && approved.isPresent() && userId.isPresent()) {
            if (storage.findById(bookingId.get()).isPresent()) {
                if (Objects.equals(storage.findById(bookingId.get()).get().getItem().getOwner(), userId.get())) {
                    if (!Boolean.getBoolean(approved.get())) {
                        storage.findById(bookingId.get()).get().setStatus(BookingStatus.APPROVED);
                    } else {
                        storage.findById(bookingId.get()).get().setStatus(BookingStatus.REJECTED);
                    }
                    storage.flush();
                    return MapperBookingDto.toBooking(storage.findById(bookingId.get()).get());
                } else {
                    throw new RuntimeException("wrong user");
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
        if (state.equals("REJECTED")) {
            return all.stream().filter(o -> o.getStatus().equals(BookingStatus.REJECTED))
                    .sorted(Comparator.comparing(BookingDto::getStart)).collect(Collectors.toList());
        }
        throw new StatusException("Unknown state: UNSUPPORTED_STATUS");

    }

    public List<BookingDto> getAllOwner(Optional<Long> userId, String state) {
        if(userId.isPresent()) {
            List<Item> userItem = itemStorage.findByOwner(userId.get());
            List<BookingDto> userBooking = new ArrayList<>();
            userItem.forEach(o -> userBooking.add(MapperBookingDto.toBooking(storage.findByItem_id(o.getId()))));
            return userBooking;
        }
        throw new RuntimeException("need user id");
    }

    }
