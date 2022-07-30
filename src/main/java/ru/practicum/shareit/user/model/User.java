package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;



import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * // TODO .
 */
@Data
@Builder
public class User {

    private  Long id;
    @NotNull
    private  String name;
    @NotNull
    @Email
    private  String email;
}
