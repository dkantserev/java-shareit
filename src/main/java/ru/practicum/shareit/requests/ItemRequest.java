package ru.practicum.shareit.requests;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * // TODO .
 */
@Data
public class ItemRequest {
    private final Long id;
    private final String description;
    private final User requestor;
    private final LocalDateTime created;
}
