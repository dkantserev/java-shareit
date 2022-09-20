package ru.practicum.server.shareit.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.server.booking.storage.BookingStorage;
import ru.practicum.server.booking.BookingStatus;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.storage.ItemStorage;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.storage.UserStorage;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
class BookingStorageTest {

    @Autowired
    BookingStorage bookingStorage;
    @Autowired
    ItemStorage itemStorage;
    @Autowired
    UserStorage userStorage;
    @Autowired
    TestEntityManager entityManager;

    @Test
    public void complexSearchTest() {

        User user1 = new User();
        user1.setName("rrr");
        user1.setEmail("ttt@mail.ru");
        User user3 = new User();
        user3.setName("ttttt");
        user3.setEmail("yyyyyy@mail.ru");
        userStorage.save(user1);
        userStorage.save(user3);
        Item item = new Item();
        item.setName("МолОток");
        item.setDescription("ЧАйник");
        item.setOwner(1L);
        item.setAvailable(true);
        itemStorage.save(item);
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.of(2022, 1, 1, 1, 1));
        booking.setEndBooking(LocalDateTime.of(2022, 1, 1, 10, 1));
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setBooker(user3);
        bookingStorage.save(booking);
        Booking booking1 = bookingStorage.ownerBooking(1L, PageRequest.of(0, 2)).getContent().get(0);
        assertEquals(booking1.getEndBooking(), booking.getEndBooking());
        Booking booking2 = bookingStorage.ownerBookingPast(1L, LocalDateTime.now(), PageRequest.of(0, 2))
                .getContent().get(0);
        assertEquals(booking2.getEndBooking(), booking.getEndBooking());
        booking.setEndBooking(LocalDateTime.of(2023, 1, 1, 10, 1));
        Booking booking3 = bookingStorage.ownerBookingWaiting(1L, BookingStatus.WAITING, LocalDateTime.now(),
                PageRequest.of(0, 2)).getContent().get(0);
        assertEquals(booking3.getEndBooking(), booking.getEndBooking());
    }

}