package ru.practicum.server.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;


import org.springframework.stereotype.Repository;
import ru.practicum.server.item.model.Item;


@Repository
public interface ItemStorage extends JpaRepository<Item,Long>,ItemSearch {

}
