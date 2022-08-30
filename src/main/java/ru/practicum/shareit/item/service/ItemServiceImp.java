package ru.practicum.shareit.item.service;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.MapperBookingDto;
import ru.practicum.shareit.booking.exception.BookingException;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.comment.storage.CommentStorage;
import ru.practicum.shareit.item.MapperItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoUpdate;
import ru.practicum.shareit.item.itemException.ItemNotFound;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.requests.exception.RequestNotFound;
import ru.practicum.shareit.requests.storage.RequestStorage;
import ru.practicum.shareit.user.UserExceptions.UserNotFound;
import ru.practicum.shareit.user.userSevice.UserServiceImp;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemServiceImp implements ItemService {

    private final ItemStorage storage;
    private final UserServiceImp userService;
    private final BookingStorage booking;
    private final CommentStorage comment;
    private final RequestStorage requestStorage;

    public ItemServiceImp(ItemStorage storage, UserServiceImp userService, BookingStorage booking, CommentStorage comment, RequestStorage requestStorage) {
        this.storage = storage;
        this.userService = userService;
        this.booking = booking;
        this.comment = comment;
        this.requestStorage = requestStorage;
    }

    @Override
    public ItemDto add(Optional<ItemDto> itemDto, Optional<Long> userId) {

        if (itemDto.isPresent() && userId.isPresent()) {
            if (userService.get(userId.get()) == null) {
                throw new RuntimeException("owner not found");
            }
            Item item = MapperItemDto.toItem(itemDto.get());
            item.setOwner(userId.get());
            if (itemDto.get().getRequestId() != null) {
                item.setRequest(requestStorage.findById(itemDto.get().getRequestId()).orElseThrow(RequestNotFound::new));
            }
            return MapperItemDto.toItemDto(storage.save(item));
        }
        throw new RuntimeException("user or item empty");

    }

    @Override
    public ItemDto update(ItemDtoUpdate itemDtoUpdate, Optional<Long> userId, Optional<Long> itemId) {
        if (userId.isPresent() && itemId.isPresent()) {
            if (storage.findById(itemId.get()).isEmpty()) {
                throw new BookingException("item not found");
            }
            if (userService.get(userId.get()) == null) {
                throw new RuntimeException("owner not found");
            }
            if (storage.findById(itemId.get()).isPresent()) {
                Item itemUpdate = storage.findById(itemId.get()).get();
                if (!Objects.equals(itemUpdate.getOwner(), userId.get())) {
                    throw new UserNotFound("owner does not match");
                }
                if (itemDtoUpdate.getName() != null) {
                    itemUpdate.setName(itemDtoUpdate.getName());
                }
                if (itemDtoUpdate.getAvailable() != null) {
                    itemUpdate.setAvailable(itemDtoUpdate.getAvailable());
                }
                if (itemDtoUpdate.getDescription() != null) {
                    itemUpdate.setDescription(itemDtoUpdate.getDescription());
                }
                if (itemDtoUpdate.getRequest() != null) {
                    itemUpdate.setRequest(itemUpdate.getRequest());
                }

                return MapperItemDto.toItemDto(storage.save(itemUpdate));
            }
        }
        throw new RuntimeException("bad param");
    }

    @Override
    public List<ItemDto> getAllItemsUser(Optional<Long> userId, Optional<Long> from, Optional<Long> size,LocalDateTime localDateTime) {
        List<ItemDto> all = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "id"));
        if (userId.isEmpty()) {
            storage.findAll().forEach(o -> all.add(MapperItemDto.toItemDto(o)));
            return all;
        }
        if(from.isPresent()&&size.isPresent()&&from.get()>0&&size.get()>0){
            pageable =PageRequest.of(from.get().intValue()/size.get().intValue(),size.get().intValue());
        }

        storage.findByOwner(userId.get(),pageable).forEach(o -> all.add(MapperItemDto.toItemDto(o)));
        all.forEach(o -> o.setComments(comment.getCommentByItemId(o.getId())));
        all.forEach(o -> fillBooking(o, userId.get(),localDateTime));
        return all.stream().sorted(Comparator.comparing(ItemDto::getId)).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(Optional<String> text, Optional<Long> from, Optional<Long> size) {
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "id"));
        int start = 0;
        int l = 20;
        if (from.isPresent() && size.isPresent()) {
            if (from.get() > 0 && size.get() > 0) {
                start = from.get().intValue() / size.get().intValue();
            }
            l = size.get().intValue();
            pageable = PageRequest.of(start, l);
        }
        List<ItemDto> all = new ArrayList<>();
        if (text.isPresent()) {
            if (text.get().isBlank()) {
                return all;
            }
            storage.findByNameContainingIgnoreCaseAndAvailableTrue(text.get().toLowerCase(), pageable).forEach(o -> all.add(MapperItemDto.toItemDto(o)));
            storage.findByDescriptionContainingIgnoreCaseAndAvailableTrue(text.get().toLowerCase(), pageable).forEach(o -> all.add(MapperItemDto.toItemDto(o)));
            int finalStart = start;
            return all.stream().distinct().filter(o -> o.getId() > finalStart).limit(l).collect(Collectors.toList());
        }
        throw new RuntimeException("");
    }

    @Override
    public ItemDto get(Long itemId, Optional<Long> userId,LocalDateTime localDateTime) {


        if (storage.findById(itemId).isPresent() && userId.isPresent()) {
            Boolean owner = Objects.equals(storage.findById(itemId).get().getOwner(), userId.get());
            ItemDto item = MapperItemDto.toItemDto(storage.findById(itemId).get());
            if (booking.findFirstByItem_idAndEndBookingBefore(itemId, localDateTime).isPresent() && owner) {
                item.setLastBooking(MapperBookingDto.toBookingItem(booking.findFirstByItem_idAndEndBookingBefore(itemId, localDateTime).get()));
            }
            if (booking.findFirstByItem_idAndStartAfter(itemId, localDateTime).isPresent() && owner) {
                item.setNextBooking(MapperBookingDto.toBookingItem(booking.findFirstByItem_idAndStartAfter(itemId, localDateTime).get()));
            }
            item.setComments(comment.getCommentByItemId(itemId));
            return item;
        }
        throw new ItemNotFound("item not found");
    }

    private void fillBooking(ItemDto itemDto, Long userId,LocalDateTime localDateTime) {
        Boolean owner = Objects.equals(storage.findById(itemDto.getId()).get().getOwner(), userId);
        if (booking.findFirstByItem_idAndEndBookingBefore(itemDto.getId(), localDateTime).isPresent() && owner) {
            itemDto.setLastBooking(MapperBookingDto.toBookingItem(booking.findFirstByItem_idAndEndBookingBefore(itemDto.getId(), localDateTime).get()));
        }
        if (booking.findFirstByItem_idAndStartAfter(itemDto.getId(), localDateTime).isPresent() && owner) {
            itemDto.setNextBooking(MapperBookingDto.toBookingItem(booking.findFirstByItem_idAndStartAfter(itemDto.getId(), localDateTime).get()));
        }
    }

}
