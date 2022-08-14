package ru.practicum.shareit.booking.storage;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface BookingSearch {
//List<Booking> findByBooker_id(Long userId);
Booking findByItem_id(Long itemId);
}
