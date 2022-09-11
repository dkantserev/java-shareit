package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import ru.practicum.shareit.booking.dto.BookingDtoForItem;

import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.requests.model.ItemRequest;


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
