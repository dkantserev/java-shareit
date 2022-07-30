package ru.practicum.shareit.item.itemException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotFoundDescriptionException extends RuntimeException {

    public NotFoundDescriptionException(String message) {
        super(message);
    }
}
