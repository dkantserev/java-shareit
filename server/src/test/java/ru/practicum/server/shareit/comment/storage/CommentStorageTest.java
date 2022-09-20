package ru.practicum.server.shareit.comment.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.server.comment.model.Comment;
import ru.practicum.server.comment.storage.CommentStorage;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.storage.ItemStorage;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
class CommentStorageTest {

    @Autowired
    CommentStorage commentStorage;
    @Autowired
    ItemStorage itemStorage;
    @Autowired
    TestEntityManager testEntityManager;

    @Test
    public void whenGetByItemId_thenPositive() {

        Item item = new Item();
        item.setName("МолОток");
        item.setDescription("ЧАйник");
        item.setAvailable(true);
        itemStorage.save(item);
        Comment comment = new Comment();
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        comment.setText("ok");
        comment.setAuthorName("JJ");
        commentStorage.save(comment);
        Long itemId = itemStorage.findByNameContainingIgnoreCaseAndAvailableTrue("молоток",
                PageRequest.of(0, 2)).getContent().get(0).getId();
        Comment comment1 = commentStorage.getCommentByItemId(itemId).get(0);
        assertEquals(item.getDescription(), comment1.getItem().getDescription());
    }

}