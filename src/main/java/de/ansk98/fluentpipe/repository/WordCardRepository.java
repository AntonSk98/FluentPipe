package de.ansk98.fluentpipe.repository;

import de.ansk98.fluentpipe.domain.WordCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing {@link WordCard} entities.
 *
 * @author ansk98
 */
public interface WordCardRepository extends JpaRepository<WordCard, UUID> {

    /**
     * Finds the non-published word card that belongs to a user.
     *
     * @param ownerId owner id
     * @return the not-published word card if present
     */
    Optional<WordCard> findByOwnerIdAndPublishedFalse(String ownerId);
}