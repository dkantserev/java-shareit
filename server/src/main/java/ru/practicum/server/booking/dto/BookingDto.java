package ru.practicum.server.booking.dto;

import lombok.*;

import ru.practicum.server.booking.BookingStatus;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.user.model.User;


import java.time.LocalDateTime;


@Data
@Builder
public class BookingDto {

    private Long id;
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private BookingStatus status;

}
