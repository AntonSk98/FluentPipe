CREATE TABLE IF NOT EXISTS word_card
(
    id       blob         not null primary key,
    owner_id varchar(255) not null,
    published boolean      not null
);

CREATE TABLE IF NOT EXISTS word
(
    id                 blob         not null primary key,
    example            varchar(255),
    example_translation varchar(255),
    frequency          varchar(255) check (frequency in ('VERY_RARE', 'RARE', 'MODERATE', 'OFTEN', 'VERY_OFTEN')),
    meaning            varchar(255),
    translation        varchar(255),
    word               varchar(255),
    word_card_id       blob,
    constraint fk_word_word_card foreign key (word_card_id) references word_card (id)
);
