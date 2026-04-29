package ru.justspeak.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.justspeak.model.mongodb.CommentDocument;

@Repository
public interface CommentRepository extends MongoRepository<CommentDocument, String> {

    Page<CommentDocument> findByPostId(Long postId, Pageable pageable);

    long countByPostId(Long postId);
}
