package ru.practicum.server.user.model;


import lombok.*;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.requests.model.ItemRequest;


import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(name = "name")
    private String name;
    @NotNull
    @Email
    @Column(name = "email")
    private String email;
    @OneToMany(mappedBy = "booker", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> booking;
    @OneToMany(mappedBy = "requestor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ItemRequest> requests;



}
