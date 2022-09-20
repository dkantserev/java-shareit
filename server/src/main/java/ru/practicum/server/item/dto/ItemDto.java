package ru.practicum.server.item.dto;

import lombok.Builder;
import lombok.Data;

import ru.practicum.server.booking.dto.BookingDtoForItem;

import ru.practicum.server.comment.model.Comment;
import ru.practicum.server.requests.model.ItemRequest;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
@Builder
public class ItemDto {

    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private Long owner;
    private ItemRequest request;
    private BookingDtoForItem lastBooking;
    private BookingDtoForItem nextBooking;
    private List<Comment> comments;
    private Long requestId;
}
