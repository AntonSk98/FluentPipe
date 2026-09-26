package de.ansk98.fluentpipe.service.impl;

import de.ansk98.fluentpipe.domain.Word;
import de.ansk98.fluentpipe.domain.WordCard;
import de.ansk98.fluentpipe.repository.WordCardRepository;
import de.ansk98.fluentpipe.service.api.WordCardService;
import de.ansk98.fluentpipe.service.api.commands.AddWordCommand;
import de.ansk98.fluentpipe.service.api.commands.DeleteWordCommand;
import de.ansk98.fluentpipe.service.api.commands.FetchActiveWordCardCommand;
import de.ansk98.fluentpipe.service.api.dto.WordCardDto;
import de.ansk98.fluentpipe.service.api.dto.WordDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementation of {@link WordCardService}.
 *
 * @author ansk98
 */
@Service
@Transactional
public class WordCardServiceImpl implements WordCardService {

    private final WordCardRepository wordCardRepository;

    /**
     * Constructor.
     *
     * @param wordCardRepository See {@link WordCardRepository}
     */
    public WordCardServiceImpl(WordCardRepository wordCardRepository) {
        this.wordCardRepository = wordCardRepository;
    }

    @Override
    public WordDto addWordToCard(AddWordCommand command) {
        WordCard card = fetchOrCreateNotPublishedWordCard(command.ownerId());
        Word word = card.findWord(command.word())
                .orElseGet(() -> {
                    Word newWord = Word.newWord(command.word(), command.translation(), command.meaning(), command.frequency(), command.example(), command.exampleTranslation());
                    card.addWord(newWord);
                    return newWord;
                });
        wordCardRepository.save(card);
        return WordDto.from(word);
    }

    @Override
    public boolean removeWordFromCard(DeleteWordCommand command) {
        return fetchNotPublishedCard(command.ownerId())
                .map(card -> removeWord(card, command.word()))
                .orElse(false);
    }

    private boolean removeWord(WordCard card, String word) {
        return card.findWord(word)
                .map(found -> {
                    card.removeWord(found);
                    wordCardRepository.save(card);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public void publishWordCard(String ownerId) {
        fetchNotPublishedCard(ownerId)
                .ifPresent(card -> {
                    card.publishCard();
                    wordCardRepository.save(card);
                });
    }

    @Override
    public void clearNotPublishedWordCard(String ownerId) {
        fetchNotPublishedCard(ownerId)
                .ifPresent(wordCardRepository::delete);
    }

    @Override
    public WordCardDto fetchActiveWordCard(FetchActiveWordCardCommand command) {
        return WordCardDto.from(fetchOrCreateNotPublishedWordCard(command.ownerId()).getWords());
    }

    private Optional<WordCard> fetchNotPublishedCard(String ownerId) {
        return wordCardRepository.findByOwnerIdAndPublishedFalse(ownerId);
    }

    private WordCard fetchOrCreateNotPublishedWordCard(String ownerId) {
        return fetchNotPublishedCard(ownerId)
                .orElseGet(() -> wordCardRepository.save(WordCard.newCard(ownerId)));
    }
}