package ru.practicum.shareit.user.model;

import lombok.*;
import ru.practicum.shareit.booking.model.Booking;


import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;




@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(name="name")
    private String name;
    @NotNull
    @Email
    @Column(name="email")
    private String email;
    @OneToOne(mappedBy = "booker", cascade = CascadeType.ALL)
    private Booking booking;



}
