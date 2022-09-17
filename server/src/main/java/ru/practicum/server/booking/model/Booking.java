package ru.practicum.server.booking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import ru.practicum.server.booking.BookingStatus;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.user.model.User;


import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table
@ToString
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime start;
    private LocalDateTime endBooking;
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id")
    @JsonIgnore
    private Item item;
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "booker_id")
    @JsonIgnore
    private User booker;
    @Enumerated(value = EnumType.STRING)
    private BookingStatus status;
}
