package ru.practicum.server.booking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingDtoForItem {
    private Long id;
    private Long bookerId;
}