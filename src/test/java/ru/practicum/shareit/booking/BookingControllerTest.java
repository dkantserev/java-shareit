package ru.practicum.shareit.booking;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @MockBean
    BookingService bookingService;
    @InjectMocks
    BookingController bookingController;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    Booking booking = new Booking();
    BookingDto bookingDto = BookingDto.builder().build();
    Long userId;
    Long from;
    Long size;
    User user = new User();

    @BeforeEach
    void fill() {

        bookingDto.setId(1L);
        userId = 1L;
        bookingDto.setStatus(BookingStatus.WAITING);
    }


    @Test
    void add() throws Exception {

        Mockito
                .when(bookingService.add(Optional.of(bookingDto), Optional.of(userId),
                        LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)))
                .thenReturn(bookingDto);
        mockMvc.perform(post("/bookings").header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(bookingDto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void get() throws Exception {

        Mockito
                .when(bookingService.get(Optional.of(1L), Optional.of(1L)))
                .thenReturn(bookingDto);
        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/1").header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void setApproved() throws Exception {

        Mockito
                .when(bookingService.setApproved(Optional.of(1L), Optional.of("true"), Optional.of(1L)))
                .thenReturn(bookingDto);
        mockMvc.perform(patch("/bookings/1").header("X-Sharer-User-Id", "1")
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void getAll() throws Exception {

        Mockito
                .when(bookingService.getAll(Optional.of(1L), "ALL", Optional.of(1L),
                        Optional.of(2L)))
                .thenReturn(List.of(bookingDto));
        mockMvc.perform(MockMvcRequestBuilders.get("/bookings").header("X-Sharer-User-Id", "1")
                        .param("state", "ALL").param("from", "1").param("size",
                                "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].status").value("WAITING"));
    }

    @Test
    void getAllOwner() throws Exception {

        Mockito
                .when(bookingService.getAllOwner(Optional.of(1L), "ALL", Optional.of(1L),
                        Optional.of(2L), LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)))
                .thenReturn(List.of(bookingDto));
        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner").header("X-Sharer-User-Id",
                        "1").param("state", "ALL").param("from",
                        "1").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].status").value("WAITING"));
    }

}