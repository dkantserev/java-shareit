package ru.practicum.server.comment.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.server.comment.model.Comment;

import java.util.List;

@Repository
public interface CommentStorage extends JpaRepository<Comment, Long> {

    @Query("select c from Comment  c where c.item.id=?1")
    public List<Comment> getCommentByItemId(Long itemId);

}
