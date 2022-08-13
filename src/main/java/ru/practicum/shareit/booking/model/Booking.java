package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;


import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * // TODO .
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    private  LocalDateTime start;
    private  LocalDateTime endBooking;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="item_id")
    private  Item item;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="booker_id")
    private  User booker;
    private BookingStatus status;
}
