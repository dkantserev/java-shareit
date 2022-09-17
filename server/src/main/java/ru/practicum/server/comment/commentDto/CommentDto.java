package ru.practicum.server.comment.commentDto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.server.item.model.Item;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {

    private Long id;
    private String text;
    private Item item;
    private String authorName;
    private LocalDateTime created;
}
