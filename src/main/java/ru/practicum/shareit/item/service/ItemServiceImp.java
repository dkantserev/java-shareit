package ru.practicum.shareit.item.service;


import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.MapperBookingDto;
import ru.practicum.shareit.booking.exception.BookingException;
import ru.practicum.shareit.booking.storage.BookingStorageInterface;
import ru.practicum.shareit.comment.storage.CommentStorage;
import ru.practicum.shareit.item.MapperItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoUpdate;
import ru.practicum.shareit.item.itemException.ItemNotFound;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.UserExceptions.UserNotFound;
import ru.practicum.shareit.user.userSevice.UserServiceImp;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemServiceImp implements ItemServiceInterface{

   private final ItemStorage storage;
   private final UserServiceImp userService;
   private final BookingStorageInterface booking;
   private final CommentStorage comment;

    public ItemServiceImp(ItemStorage storage, UserServiceImp userService, BookingStorageInterface booking, CommentStorage comment) {
        this.storage = storage;
        this.userService = userService;
        this.booking = booking;
        this.comment = comment;
    }

    @Override
    public ItemDto add(Optional<ItemDto> itemDto, Optional<Long> userId) {

        if(itemDto.isPresent()&& userId.isPresent()){
            if(userService.get(userId.get())==null){
                throw new RuntimeException("owner not found");
            }
            Item item = MapperItemDto.toItem(itemDto.get());
            item.setOwner(userId.get());
        return MapperItemDto.toItemDto(storage.save(item));


        }
        throw new RuntimeException("user or item empty");

    }

    @Override
    public ItemDto update(ItemDtoUpdate itemDtoUpdate, Optional<Long> userId, Optional<Long> itemId) {
        if(userId.isPresent()&&itemId.isPresent()){
            if(storage.findById(itemId.get()).isEmpty()){
                throw new BookingException("item not found");
            }
            if(userService.get(userId.get())==null){
                throw new RuntimeException("owner not found");
            }
            if(storage.findById(itemId.get()).isPresent()) {
                Item itemUpdate = storage.findById(itemId.get()).get();
                if(!Objects.equals(itemUpdate.getOwner(), userId.get())){
                    throw new UserNotFound("owner does not match");
                }
                if(itemDtoUpdate.getName()!=null) {
                    itemUpdate.setName(itemDtoUpdate.getName());
                }
                if(itemDtoUpdate.getAvailable()!=null) {
                    itemUpdate.setAvailable(itemDtoUpdate.getAvailable());
                }
                if(itemDtoUpdate.getDescription()!=null) {
                    itemUpdate.setDescription(itemDtoUpdate.getDescription());
                }
                if(itemDtoUpdate.getRequest()!=null) {
                    itemUpdate.setRequest(itemUpdate.getRequest());
                }

                return MapperItemDto.toItemDto(storage.save(itemUpdate));
            }
        }
        throw new RuntimeException("bad param");
    }

    @Override
    public List<ItemDto> getAllItemsUser(Optional<Long> userId) {
        List<ItemDto> all = new ArrayList<>();
        if(userId.isEmpty()) {
            storage.findAll().forEach(o -> all.add(MapperItemDto.toItemDto(o)));
            return all;
        }

        storage.findByOwner(userId.get()).forEach(o->all.add(MapperItemDto.toItemDto(o)));
        all.forEach(o->o.setComments(comment.getCommentByItemId(o.getId())));
        all.forEach(o->fillBooking(o,userId.get()));
        return all.stream().sorted(Comparator.comparing(ItemDto::getId)).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(Optional<String> text) {
        List<ItemDto> all = new ArrayList<>();
        if(text.isPresent()) {
            if(text.get().isBlank()){
                return all;
            }
            storage.findByNameContainingIgnoreCaseAndAvailableTrue(text.get().toLowerCase()).forEach(o->all.add(MapperItemDto.toItemDto(o)));
            storage.findByDescriptionContainingIgnoreCaseAndAvailableTrue(text.get().toLowerCase()).forEach(o->all.add(MapperItemDto.toItemDto(o)));
            return all.stream().distinct().collect(Collectors.toList());
        }

        throw new RuntimeException("");
    }

    @Override
    public ItemDto get(Long itemId,Optional<Long> userId) {


        if(storage.findById(itemId).isPresent()&&userId.isPresent()) {
            Boolean owner = Objects.equals(storage.findById(itemId).get().getOwner(), userId.get());
            ItemDto item=MapperItemDto.toItemDto(storage.findById(itemId).get());
            if(booking.findFirstByItem_idAndEndBookingBefore(itemId, LocalDateTime.now()).isPresent()&&owner){
                item.setLastBooking(MapperBookingDto.toBookingItem(booking.findFirstByItem_idAndEndBookingBefore(itemId, LocalDateTime.now()).get()));
            }
            if(booking.findFirstByItem_idAndStartAfter(itemId, LocalDateTime.now()).isPresent()&&owner){
                item.setNextBooking(MapperBookingDto.toBookingItem(booking.findFirstByItem_idAndStartAfter(itemId, LocalDateTime.now()).get()));
            }
            item.setComments(comment.getCommentByItemId(itemId));
            return item;
        }
        throw new ItemNotFound("item not found");
    }

    private void fillBooking(ItemDto itemDto,Long userId){
        Boolean owner = Objects.equals(storage.findById(itemDto.getId()).get().getOwner(), userId);
        if(booking.findFirstByItem_idAndEndBookingBefore(itemDto.getId(), LocalDateTime.now()).isPresent()&&owner){
            itemDto.setLastBooking(MapperBookingDto.toBookingItem(booking.findFirstByItem_idAndEndBookingBefore(itemDto.getId(), LocalDateTime.now()).get()));
        }
        if(booking.findFirstByItem_idAndStartAfter(itemDto.getId(), LocalDateTime.now()).isPresent()&&owner){
            itemDto.setNextBooking(MapperBookingDto.toBookingItem(booking.findFirstByItem_idAndStartAfter(itemDto.getId(), LocalDateTime.now()).get()));
        }
    }

}
