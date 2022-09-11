package ru.practicum.shareit.requests.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.service.RequestService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;


@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final RequestService requestService;

    public ItemRequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ItemRequestDto add(@Valid @RequestBody Optional<ItemRequestDto> request,
                              @RequestHeader("X-Sharer-User-Id") Optional<Long> userId) {
        log.info("add request " + request.orElse(null) + " with userId " + userId.orElse(null));
        return requestService.add(request, userId);
    }

    @GetMapping
    public List<ItemRequestDto> get(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId) {
        log.info("get request  with userId " + userId.orElse(null));
        return requestService.get(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto get(@PathVariable Long requestId,
                              @RequestHeader("X-Sharer-User-Id") Optional<Long> userId) {
        log.info("get request with requestId " + requestId + "  with userId " + userId.orElse(null));
        return requestService.getForId(requestId, userId);
    }

    @GetMapping("all")
    public List<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                       @RequestParam Optional<Long> from,
                                       @RequestParam Optional<Long> size) {
        log.info("getAll request with userId " + userId.orElse(null) + " with " + from.orElse(null) +
                " and size " + size.orElse(null));
        return requestService.getAll(userId, from, size);
    }

}
