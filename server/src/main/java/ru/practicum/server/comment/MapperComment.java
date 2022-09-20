package ru.practicum.server.comment;

import ru.practicum.server.comment.commentDto.CommentDto;
import ru.practicum.server.comment.model.Comment;

public class MapperComment {
    public static Comment toComment(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setAuthorName(commentDto.getAuthorName());
        comment.setItem(commentDto.getItem());
        comment.setText(commentDto.getText());
        comment.setCreated(commentDto.getCreated());
        return comment;
    }

    public static CommentDto commentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .authorName(comment.getAuthorName())
                .text(comment.getText())
                .item(comment.getItem())
                .created(comment.getCreated()).build();
    }
}
