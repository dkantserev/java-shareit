package ru.practicum.server.requests.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.server.requests.model.ItemRequest;

@Repository
public interface RequestStorage extends JpaRepository<ItemRequest,Long>, RequestSearch {

}
