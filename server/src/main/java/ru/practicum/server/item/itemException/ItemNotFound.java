package ru.practicum.server.item.itemException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ItemNotFound extends RuntimeException {

    public ItemNotFound(String message) {
        super(message);
    }
}
