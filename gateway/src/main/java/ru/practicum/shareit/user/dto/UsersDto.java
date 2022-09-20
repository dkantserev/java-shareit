package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;


@Data
public class UsersDto {
    private Long id;
    @NotNull
    private String name;
    @NotNull
    @Email
    private String email;
}
