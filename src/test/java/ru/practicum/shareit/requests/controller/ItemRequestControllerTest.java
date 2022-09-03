package ru.practicum.shareit.requests.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.service.RequestService;
import ru.practicum.shareit.user.UserController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @MockBean
    RequestService requestService;
    @InjectMocks
    ItemRequestController itemRequestController;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    ItemRequestDto itemRequestDto = ItemRequestDto.builder().build();
    LocalDateTime localDateTime = LocalDateTime.of(1111, 1, 1, 1, 1, 1, 1);
    Optional<Long> userId = Optional.of(1L);

    @Test
    void add() throws Exception {
        itemRequestDto.setCreated(localDateTime);
        itemRequestDto.setDescription("ggg");
        Mockito
                .when(requestService.add(Optional.of(itemRequestDto), userId))
                .thenReturn(itemRequestDto);
        mockMvc.perform((post("/requests").header("X-Sharer-User-Id", 1))
                        .content(objectMapper.writeValueAsString(itemRequestDto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("ggg"));
    }

    @Test
    void get() throws Exception {
        itemRequestDto.setCreated(localDateTime);
        itemRequestDto.setDescription("ggg");
        Mockito
                .when(requestService.get(userId))
                .thenReturn(List.of(itemRequestDto));
        mockMvc.perform(MockMvcRequestBuilders.get("/requests").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("ggg"));
    }

    @Test
    void testGet() throws Exception {
        itemRequestDto.setCreated(localDateTime);
        itemRequestDto.setDescription("ggg");
        Mockito
                .when(requestService.getForId(1L, userId))
                .thenReturn(itemRequestDto);
        mockMvc.perform(MockMvcRequestBuilders.get("/requests/1").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("ggg"));
    }

    @Test
    void getAll() throws Exception {
        itemRequestDto.setCreated(localDateTime);
        itemRequestDto.setDescription("ggg");
        Mockito
                .when(requestService.getAll(userId, Optional.of(1L), Optional.of(1L)))
                .thenReturn(List.of(itemRequestDto));
        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all").header("X-Sharer-User-Id", 1)
                        .param("from", "1").param("from", "1").param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("ggg"));
    }
}