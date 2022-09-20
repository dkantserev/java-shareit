package ru.practicum.server.shareit.comment;

import org.junit.jupiter.api.Test;
import ru.practicum.server.comment.MapperComment;
import ru.practicum.server.comment.commentDto.CommentDto;
import ru.practicum.server.comment.model.Comment;
import ru.practicum.server.item.model.Item;


import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

class MapperCommentTest {

    Item item = new Item();
    LocalDateTime localDateTime = LocalDateTime.now();

    @Test
    void toComment() {

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setAuthorName("name");
        comment.setItem(item);
        comment.setText("text");
        comment.setCreated(localDateTime);
        CommentDto commentDto = MapperComment.commentDto(comment);
        assertEquals(commentDto.getId(), comment.getId());
        assertEquals(commentDto.getAuthorName(), comment.getAuthorName());
        assertEquals(commentDto.getItem(), comment.getItem());
        assertEquals(commentDto.getText(), comment.getText());
        assertEquals(commentDto.getCreated(), comment.getCreated());
    }

    @Test
    void commentDto() {

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .authorName("name")
                .text("text")
                .item(item)
                .created(localDateTime).build();
        Comment comment = MapperComment.toComment(commentDto);
        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getAuthorName(), commentDto.getAuthorName());
        assertEquals(comment.getItem(), commentDto.getItem());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getCreated(), commentDto.getCreated());
    }
}