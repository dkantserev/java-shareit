package ru.practicum.server.requests.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.server.requests.model.ItemRequest;


import java.util.Optional;

public interface RequestSearch {
    Optional<ItemRequest> findByRequestor_idOrderByCreated(Long id);

    Optional<ItemRequest> findByRequestor_idOrderByCreatedDesc(Long id);

    Page<ItemRequest> findByRequestor_idNotOrderByCreatedDesc(Long id, Pageable pageable);
}
