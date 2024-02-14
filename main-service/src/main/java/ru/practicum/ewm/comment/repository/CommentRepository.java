package ru.practicum.ewm.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.comment.dto.CommentCountDto;
import ru.practicum.ewm.comment.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByEventId(Long eventId, Pageable pageable);

    List<Comment> findAllByAuthorId(Long userId, Pageable pageable);

    @Query("select c from Comment as c " +
            "where lower(c.text) like concat('%', lower(?1), '%') ")
    List<Comment> findAllByText(String text);

    @Query("select new ru.practicum.ewm.comment.dto.CommentCountDto(c.event.id, count(c.id)) " +
            "from Comment as c " +
            "where c.event.id in ?1 " +
            "group by c.event.id")
    List<CommentCountDto> getCommentCountByEventIds(List<Long> eventIds);

}
