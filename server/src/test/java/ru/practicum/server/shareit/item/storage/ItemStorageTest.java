package ru.practicum.server.shareit.item.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.server.item.storage.ItemStorage;
import ru.practicum.server.item.model.Item;

import static org.junit.Assert.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
class ItemStorageTest {

    @Autowired
    ItemStorage itemStorage;
    @Autowired
    TestEntityManager entityManager;

    @Test
    public void whenSearchByNameAndByDescriptionAndMethodSave_thenPositive() {

        Item item = new Item();
        item.setName("МолОток");
        item.setDescription("ЧАйник");
        item.setAvailable(true);
        Item item2 = new Item();
        item2.setName("кот");
        item2.setDescription("носорог");
        item2.setAvailable(true);
        itemStorage.save(item);
        itemStorage.save(item2);
        Item item3 = itemStorage.findByNameContainingIgnoreCaseAndAvailableTrue("молоток",
                PageRequest.of(0, 5)).getContent().get(0);
        Item item4 = itemStorage.findByDescriptionContainingIgnoreCaseAndAvailableTrue("носорог",
                PageRequest.of(0, 5)).getContent().get(0);
        assertEquals(item.getName(), item3.getName());
        assertEquals(item2.getDescription(), item4.getDescription());
    }
}