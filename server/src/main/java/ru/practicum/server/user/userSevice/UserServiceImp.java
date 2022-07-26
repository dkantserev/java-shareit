package ru.practicum.server.user.userSevice;

import org.springframework.stereotype.Service;
import ru.practicum.server.user.MapperUserDto;
import ru.practicum.server.user.UserExceptions.UserNotFound;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.dto.UserDtoUpdate;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;


@Service
public class UserServiceImp implements UserService {

    private final UserStorage storage;

    public UserServiceImp(UserStorage storage) {
        this.storage = storage;
    }


    @Override
    public UserDto add(UserDto userDto) {

        return MapperUserDto.toUserDto(storage.save(MapperUserDto.toUser(userDto)));
    }

    @Override
    public List<UserDto> getAll() {

        List<UserDto> all = new ArrayList<>();
        storage.findAll().forEach(o -> all.add(MapperUserDto.toUserDto(o)));
        return all;
    }

    @Override
    public UserDto update(Long id, UserDtoUpdate userDto) {

        User user = storage.findById(id).orElse(null);
        if (user == null) {
            throw new UserNotFound("user not found");
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        storage.saveAndFlush(user);
        return MapperUserDto.toUserDto(user);
    }

    @Override
    public UserDto get(Long id) {

        if (storage.findById(id).isPresent()) {
            return MapperUserDto.toUserDto(storage.findById(id).get());
        }
        throw new UserNotFound("user not found");
    }

    @Override
    public void delete(long userId) {
        storage.deleteById(userId);
    }
}
