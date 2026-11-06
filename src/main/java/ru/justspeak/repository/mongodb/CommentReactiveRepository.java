package ru.justspeak.repository.mongodb;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.justspeak.model.mongodb.CommentDocument;

@Repository
public interface CommentReactiveRepository extends ReactiveMongoRepository<CommentDocument, String> {

    Flux<CommentDocument> findAllByPostId(Long postId);

    Flux<CommentDocument> findAllByUserId(Long userId);
}

