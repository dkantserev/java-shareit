package ru.practicum.shareit.requests.dto;


import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;


import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemRequestDto {


    private Long id;
    @NotEmpty
    private String description;

    private User requestor;

    private LocalDateTime created;
    private List<ItemDto> items;
}
