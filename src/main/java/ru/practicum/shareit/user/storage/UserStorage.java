package ru.practicum.shareit.user.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Repository
public interface UserStorage extends JpaRepository<User,Long>,UserSearch {
 /* User add(User user);

    User get(Long id);

    User update(User user);

    List<User> getAll();

    User delete(Long id);

    Long getNextId();*/

}
