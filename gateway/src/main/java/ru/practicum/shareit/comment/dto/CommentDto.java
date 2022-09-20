package ru.practicum.shareit.comment.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;


@Data
public class CommentDto {

    @NotEmpty
    private String text;

}
