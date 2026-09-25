package de.ansk98.fluentpipe.service.impl;

import de.ansk98.fluentpipe.config.CommandProperties;
import de.ansk98.fluentpipe.domain.ConversationState;
import de.ansk98.fluentpipe.repository.ConversationStateStore;
import de.ansk98.fluentpipe.service.api.ConversationContextService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of {@link ConversationContextService}.
 *
 * @author ansk98
 */
@Service
public class ConversationContextServiceImpl implements ConversationContextService {

    private final ConversationStateStore conversationStateStore;
    private final CommandProperties commandProperties;

    /**
     * Constructor.
     *
     * @param conversationStateStore See {@link ConversationStateStore}
     * @param commandProperties      See {@link CommandProperties}
     */
    public ConversationContextServiceImpl(ConversationStateStore conversationStateStore, CommandProperties commandProperties) {
        this.conversationStateStore = conversationStateStore;
        this.commandProperties = commandProperties;
    }

    @Override
    public boolean hasActiveCommand(String ownerId) {
        return conversationStateStore.hasActiveCommand(ownerId);
    }

    @Override
    public Optional<String> fetchActiveCommand(String ownerId) {
        if (!hasActiveCommand(ownerId)) {
            return Optional.empty();
        }

        return Optional.of(conversationStateStore.requireActiveCommand(ownerId));
    }

    @Override
    public Optional<String> fetchCallbackForActiveCommand(String ownerId) {
        return fetchActiveCommand(ownerId)
                .map(commandProperties::requireCallbackFor);
    }

    @Override
    public void startCommand(String ownerId, String command) {
        conversationStateStore.markAsOngoingCommand(ConversationState.create(ownerId, command));
    }

    @Override
    public void clearCommand(String ownerId) {
        conversationStateStore.finishCommand(ownerId);
    }
}
