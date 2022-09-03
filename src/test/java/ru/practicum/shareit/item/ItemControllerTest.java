package ru.practicum.shareit.item;


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
import ru.practicum.shareit.comment.commentDto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoUpdate;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ItemController.class)
class ItemControllerTest {
    @MockBean
    ItemService itemService;
    @MockBean
    CommentService commentService;
    @InjectMocks
    ItemController itemController;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    User user = new User();
    ItemDto itemDto = ItemDto.builder().build();
    Comment comment = new Comment();
    CommentDto commentDto = CommentDto.builder().build();
    Long userId = 1L;
    LocalDateTime localDateTime = LocalDateTime.of(1111, 1, 1, 1, 1, 1, 1);
    Long from = 1L;
    Long size = 2L;

    @BeforeEach
    void fill() {
        user.setId(1L);
        user.setName("gg");
        itemDto.setName("hh");
        itemDto.setDescription("tt");
        itemDto.setAvailable(true);
        comment.setText("ff");
        commentDto.setText("ff");
    }

    @Test
    void add() throws Exception {
        Mockito
                .when(itemService.add(Optional.of(itemDto), Optional.of(userId)))
                .thenReturn(itemDto);
        mockMvc.perform(post("/items").header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("hh"));
    }

    @Test
    void update() throws Exception {
        ItemDtoUpdate itemDtoUpdate = new ItemDtoUpdate();
        itemDtoUpdate.setName("pp");
        Mockito
                .when(itemService.update(itemDtoUpdate, Optional.of(1L), Optional.of(1L)))
                .thenReturn(itemDto);
        mockMvc.perform(patch("/items/1").header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDtoUpdate)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("hh"));
    }

    @Test
    void get() throws Exception {
        Mockito
                .when(itemService.get(1L, Optional.of(userId), LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)))
                .thenReturn(itemDto);
        mockMvc.perform(MockMvcRequestBuilders.get("/items/1").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("tt"));
    }

    @Test
    void getAll() throws Exception {
        Mockito
                .when(itemService.getAllItemsUser(Optional.of(userId), Optional.of(from), Optional.of(size),
                        LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)))
                .thenReturn(List.of(itemDto));
        mockMvc.perform(MockMvcRequestBuilders.get("/items").header("X-Sharer-User-Id", 1)
                        .param("from", "1").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].description").value("tt"));
    }

    @Test
    void search() throws Exception {
        Mockito
                .when(itemService.search(Optional.of("qq"), Optional.of(from), Optional.of(size)))
                .thenReturn(List.of(itemDto));
        mockMvc.perform(MockMvcRequestBuilders.get("/items/search").param("text", "qq")
                        .param("from", "1").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].description").value("tt"));
    }

    @Test
    void addComment() throws Exception {
        Mockito
                .when(commentService.add(Optional.of(2L), Optional.of(2L), comment,
                        LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)))
                .thenReturn(commentDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/items/2/comment").header("X-Sharer-User-Id", 2)
                .content(objectMapper.writeValueAsString(comment)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}