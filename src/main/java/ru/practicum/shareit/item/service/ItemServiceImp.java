package ru.practicum.shareit.item.service;


import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.MapperItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoUpdate;
import ru.practicum.shareit.item.itemException.ItemNotFound;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.UserExceptions.UserNotFound;
import ru.practicum.shareit.user.userSevice.UserServiceImp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemServiceImp implements ItemServiceInterface{

   private final ItemStorage storage;
   private final UserServiceImp userService;

    public ItemServiceImp(ItemStorage storage, UserServiceImp userService) {
        this.storage = storage;
        this.userService = userService;
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
                throw new ItemNotFound("item not found");
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
        return all ;
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
    public ItemDto get(Long itemId) {
        if(storage.findById(itemId).isPresent()) {
            return MapperItemDto.toItemDto(storage.findById(itemId).get());
        }
        throw new ItemNotFound("item not found");
    }
}
