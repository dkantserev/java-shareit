package ru.practicum.server.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.BookingService;
import ru.practicum.server.comment.model.Comment;
import ru.practicum.server.comment.service.CommentService;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.service.ItemService;
import ru.practicum.server.item.storage.ItemStorage;
import ru.practicum.server.requests.dto.ItemRequestDto;
import ru.practicum.server.requests.service.RequestServiceImpl;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.dto.UserDtoUpdate;
import ru.practicum.server.user.storage.UserStorage;
import ru.practicum.server.user.userSevice.UserServiceImp;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ShareItTests {

    @Autowired
    UserServiceImp userServiceImp;
    @Autowired
    ItemService itemService;
    @Autowired
    BookingService bookingService;
    @Autowired
    RequestServiceImpl requestService;
    @Autowired
    CommentService commentService;
    @Autowired
    UserStorage userStorage;
    @Autowired
    ItemStorage itemStorage;


    @Test
    void complexIntegrationTestOfTheMainLogic() {

        UserDto user1 = UserDto.builder().build();
        user1.setName("rrr");
        user1.setEmail("ttt@mail.ru");
        UserDto userBooker = UserDto.builder().build();
        userBooker.setName("booker");
        userBooker.setEmail("ggg@mail.ru");
        userServiceImp.add(user1);

        assertEquals(user1.getName(), userServiceImp.get(1L).getName());

        UserDtoUpdate userDtoUpdate = UserDtoUpdate.builder().build();
        userDtoUpdate.setName("qqq");
        userServiceImp.update(1L, userDtoUpdate);

        assertEquals(userDtoUpdate.getName(), userServiceImp.get(1L).getName());

        userServiceImp.delete(1L);

        assertThrows(RuntimeException.class, () -> userServiceImp.get(1L));

        userServiceImp.add(user1);
        userServiceImp.add(userBooker);

        assertEquals(user1.getName(), userServiceImp.get(2L).getName());

        ItemDto item = ItemDto.builder().build();
        item.setName("МолОток");
        item.setDescription("ЧАйник");
        item.setOwner(2L);
        item.setAvailable(true);
        itemService.add(Optional.of(item), Optional.of(2L));

        assertEquals(itemService.get(1L, Optional.of(2L), LocalDateTime.now()).getName(), item.getName());
        assertEquals(itemService.search(Optional.of("молоток"), Optional.of(0L), Optional.of(2L))
                .get(0).getName(), item.getName());

        BookingDto booking = BookingDto.builder().build();
        booking.setStart(LocalDateTime.of(2022, 12, 1, 1, 1));
        booking.setEnd(LocalDateTime.of(2022, 12, 1, 10, 1));
        booking.setItemId(1L);
        bookingService.add(Optional.of(booking), Optional.of(3L), LocalDateTime.now());

        assertEquals(bookingService.get(Optional.of(1L), Optional.of(3L)).getStatus(), booking.getStatus());

        bookingService.getAllOwner(Optional.of(2L), "ALL", Optional.of(0L), Optional.of(2L),
                LocalDateTime.now()).get(0);

        assertEquals(bookingService.getAllOwner(Optional.of(2L), "ALL", Optional.of(0L),
                Optional.of(2L), LocalDateTime.now()).get(0).getEnd(), booking.getEnd());

        ItemRequestDto itemRequestDto = ItemRequestDto.builder().build();
        itemRequestDto.setDescription("молотить по стенам");
        requestService.add(Optional.of(itemRequestDto), Optional.of(2L));

        assertEquals(requestService.get(Optional.of(2L)).get(0).getDescription(), itemRequestDto.getDescription());

        Comment comment = new Comment();
        comment.setText("normal");
        commentService.add(Optional.of(3L), Optional.of(1L), comment,
                LocalDateTime.of(2022, 12, 1, 10, 2));

        assertEquals(itemService.get(1L, Optional.of(2L),
                LocalDateTime.now()).getComments().get(0).getText(), comment.getText());


    }

}
