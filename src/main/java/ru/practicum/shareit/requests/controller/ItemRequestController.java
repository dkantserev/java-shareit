package ru.practicum.shareit.requests.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.service.RequestService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final RequestService requestService;

    public ItemRequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ItemRequestDto add(@Valid @RequestBody Optional<ItemRequestDto> request,
                              @RequestHeader("X-Sharer-User-Id") Optional<Long> userId){
        return requestService.add(request,userId);
    }

    @GetMapping
    public List<ItemRequestDto> get(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId){
        return requestService.get(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto get(@PathVariable Long requestId,
                              @RequestHeader("X-Sharer-User-Id") Optional<Long> userId){
        return requestService.getForId(requestId,userId);
    }

    @GetMapping("all")
    public List<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                       @RequestParam Optional<Long> from,
                                       @RequestParam Optional<Long> size){
        return requestService.getAll(userId,from,size);
    }

}
