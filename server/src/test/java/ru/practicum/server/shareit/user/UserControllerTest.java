package ru.practicum.server.shareit.user;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.server.user.UserController;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.dto.UserDtoUpdate;
import ru.practicum.server.user.userSevice.UserService;
import ru.practicum.server.user.userSevice.UserServiceImp;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

    @MockBean
    UserServiceImp userServiceImp;
    @InjectMocks
    UserController userController;

    @Autowired
    public MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Mock
    UserService userService;

    @Test
    public void whenPostUser_thenPositive() throws Exception {

        UserDto userDto = UserDto.builder().build();
        userDto.setName("ww");
        userDto.setEmail("www@ww.ru");
        Mockito
                .when(userServiceImp.add(userDto))
                .thenReturn(userDto);
        mockMvc.perform(post("/users").content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ww"))
                .andExpect(jsonPath("$.email").value("www@ww.ru"));
    }

    @Test
    public void whenGetAllUser_thenPositive() throws Exception {

        UserDto userDto = UserDto.builder().build();
        userDto.setName("ww");
        userDto.setEmail("www@ww.ru");
        List<UserDto> userList = List.of(userDto);
        Mockito
                .when(userServiceImp.getAll())
                .thenReturn(userList);
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("ww"))
                .andExpect(jsonPath("$[0].email").value("www@ww.ru"));
    }

    @Test
    public void whenPatchUser_thenPositive() throws Exception {

        UserDto userDto = UserDto.builder().build();
        userDto.setName("ww");
        userDto.setEmail("www@ww.ru");
        UserDtoUpdate userDtoUpdate = UserDtoUpdate.builder().build();
        userDtoUpdate.setName("yyy");
        userDtoUpdate.setEmail("www@ww.ru");
        Mockito
                .when(userServiceImp.update(1L, userDtoUpdate))
                .thenReturn(userDto);
        mockMvc.perform(patch("/users/1").content(objectMapper.writeValueAsString(userDtoUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ww"))
                .andExpect(jsonPath("$.email").value("www@ww.ru"));
    }

    @Test
    public void whenGetID_thenPositive() throws Exception {

        UserDto userDto = UserDto.builder().build();
        userDto.setName("ww");
        userDto.setEmail("www@ww.ru");
        Mockito
                .when(userServiceImp.get(1L))
                .thenReturn(userDto);
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ww"))
                .andExpect(jsonPath("$.email").value("www@ww.ru"));
    }

    @Test
    public void whenDelete_thenPositive() throws Exception {

        Mockito.doNothing().when(userServiceImp).delete(1L);
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
        Mockito.verify(userServiceImp, Mockito.times(1)).delete(1L);
    }


}