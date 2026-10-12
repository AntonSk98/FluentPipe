package de.ansk98.fluentpipe.repository;

import de.ansk98.fluentpipe.model.FeedSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository infrastructure for managing {@link FeedSubscription} states.
 *
 * <p>Handles standard transactional CRUD operations over persistent configuration listings
 * mapped directly to backing RDBMS stores.</p>
 *
 * @author ansk98
 */
@Repository
public interface FeedSubscriptionRepository extends JpaRepository<FeedSubscription, String> {
}
