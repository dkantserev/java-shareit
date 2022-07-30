package ru.practicum.shareit.user.UserExceptions;

public class CreateUserException extends RuntimeException{
    public CreateUserException(String message) {
        super(message);
    }
}
