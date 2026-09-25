package de.ansk98.fluentpipe.repository;

import de.ansk98.fluentpipe.domain.Word;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository interface for managing {@link Word} entities.
 *
 * @author ansk98
 */
public interface WordRepository extends JpaRepository<Word, UUID> {
}