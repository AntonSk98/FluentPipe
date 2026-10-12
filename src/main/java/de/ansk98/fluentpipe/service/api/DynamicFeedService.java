package de.ansk98.fluentpipe.service.api;

import de.ansk98.fluentpipe.service.api.commands.RegisterFeedCommand;
import de.ansk98.fluentpipe.service.api.commands.UnregisterFeedCommand;
import de.ansk98.fluentpipe.service.api.dto.RegisteredFeedDto;

import java.util.List;

/**
 * Service for dynamically registering, retrieving, and unregistering RSS feeds at runtime.
 *
 * @author ansk98
 */
public interface DynamicFeedService {

    /**
     * Registers a new RSS feed for polling and consumption.
     *
     * @param command the command containing the registration details and target feed URL
     */
    void registerFeed(RegisterFeedCommand command);

    /**
     * Retrieves all currently active and registered RSS feeds.
     *
     * @return a list of {@link RegisteredFeedDto} objects representing active feeds,
     * or an empty list if no active feeds exist
     */
    List<RegisteredFeedDto> findAllActiveFeeds();

    /**
     * Unregisters an active RSS feed, stopping subsequent polling and updates.
     *
     * @param command the command containing the details of the feed to unregister
     */
    void unregisterFeed(UnregisterFeedCommand command);
}