package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> add(@Valid @RequestBody RequestDto request,
                                      @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("add request " + request + " with userId " + userId);
        return requestClient.add(request, userId);
    }

    @GetMapping
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("get request  with userId " + userId);
        return requestClient.getRequest(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> get(@PathVariable Long requestId,
                                      @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("get request with requestId " + requestId + "  with userId " + userId);
        return requestClient.getForId(requestId, userId);
    }

    @GetMapping("all")
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") long from,
                                         @Positive @RequestParam(name = "size", defaultValue = "10") long size) {
        log.info("getAll request with userId " + userId + " with " + from +
                " and size " + size);
        return requestClient.getAll(userId, from, size);
    }
}
