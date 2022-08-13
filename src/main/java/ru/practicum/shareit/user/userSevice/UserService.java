package ru.practicum.shareit.user.userSevice;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.MapperUserDto;
import ru.practicum.shareit.user.UserExceptions.DuplicateEmailException;
import ru.practicum.shareit.user.UserExceptions.UserNotFound;
import ru.practicum.shareit.user.UserExceptions.UserUpdateException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoUpdate;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
@Service
public class UserService implements UserServiceInterface {

    private final UserStorage storage;

    public UserService(@Qualifier("inMemoryUserStorage") UserStorage storage) {
        this.storage = storage;
    }

    @Override
    public UserDto add(UserDto userDto) {
        User user = MapperUserDto.toUser(userDto);
        if (validatorDuplicateEmail(user.getEmail())) {
            user.setId(addId());
            storage.add(user);
        } else {
            throw new DuplicateEmailException("duplicate email");
        }
        return MapperUserDto.toUserDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        List<UserDto> users = new ArrayList<>();
        storage.getAll().forEach(o -> users.add(MapperUserDto.toUserDto(o)));
        return users;
    }

    @Override
    public UserDto update(Long id, UserDtoUpdate userDto) {
        if (userDto.getEmail() != null) {
            if (userDtoEmailValidation(userDto)) {
                throw new UserUpdateException("bad email");
            }
            if (validatorDuplicateEmail(userDto.getEmail())) {
                storage.get(id).setEmail(userDto.getEmail());
            } else {
                throw new DuplicateEmailException("duplicate email");
            }
        }
        if (userDto.getName() != null) {
            storage.get(id).setName(userDto.getName());
        }
        return MapperUserDto.toUserDto(storage.get(id));
    }

    @Override
    public UserDto get(Long id) {
        if (storage.get(id) == null) {
            throw new UserNotFound("user not found");
        }
        return MapperUserDto.toUserDto(storage.get(id));
    }

    @Override
    public void delete(long userId) {
        if (storage.get(userId) == null) {
            throw new UserNotFound("user not found");
        }
        storage.delete(userId);
    }

    private boolean validatorDuplicateEmail(String email) {
        return storage.getAll().stream().noneMatch(o -> Objects.equals(o.getEmail(), email));
    }

    private Long addId() {
        return storage.getNextId();
    }

    private boolean userDtoEmailValidation(UserDtoUpdate userDto) {

        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\" +
                ".[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern p = Pattern.compile(ePattern);
        Matcher m = p.matcher(userDto.getEmail());
        return !m.matches();

    }


}*/
