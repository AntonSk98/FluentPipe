# FluentPipe

**A Telegram bot that turns vocabulary into a daily German-learning ritual.**

FluentPipe is your personal language-learning publisher. It picks a topic, builds a themed
vocabulary card, explains every word, turns the card into shareable images, narrates it with
natural-sounding audio, and posts the whole package into your Telegram channel, automatically or
whenever you feel like publishing.

No spreadsheets, no copy-pasting, no "I'll do it later". Just open Telegram and learn.

**See it live: [join Daily Deutsch on Telegram](https://t.me/daily_deutsch_group)**, the channel
where FluentPipe publishes its cards every day.

![Java](https://img.shields.io/badge/Java-powered-orange)
![Spring](https://img.shields.io/badge/Spring-Boot-6DB33F)
![Docker](https://img.shields.io/badge/Docker-ready-blue)
![Image](https://img.shields.io/badge/ghcr.io-antonsk98%2Ffluentpipe-blue)
![License](https://img.shields.io/badge/License-Apache%202.0-blue)

## What you get

- **A fresh card, ready to consume.** Every entry comes with its translation, a short definition, a
  usage-frequency rating and an example sentence in context, not just a bare word list.
- **Beautiful output, not plain text.** Cards are rendered as polished images that look great on
  mobile and can be forwarded or saved by your audience.
- **Audio included.** Every card is spoken out loud, so you can learn on the go, hands-free.
- **Fully automated.** The bot publishes on its own schedule, so consistency happens even on busy
  days.
- **Curate your own cards.** Add, remove, preview and publish vocabulary through simple bot
  commands, and keep cards unpublished until you are happy with them.
- **News on the side.** Curated RSS feeds are streamed into the same chat, formatted and ready to
  read.
- **Built to last.** Vocabulary and feed state are persisted, so restarts and redeploys do not lose
  anything or spam old articles.

## Who it is for

Language learners who want a steady stream of practical German, teachers and community owners who
run a learning channel, and tinkerers who like seeing AI, automation and a well-designed pipeline in
one project.

## How it works

```text
You (Telegram) ──> FluentPipe ──> AI analysis ──> card images + spoken audio ──> your channel
                        │
                        └────────> curated RSS news ──> formatted posts
```

The bot listens for commands from its owner. Words are analyzed by an LLM, rendered through an
HTML-to-PDF-to-image pipeline, and voiced with text-to-speech. Scheduled jobs publish cards without
any interaction, while the RSS side polls configured feeds and posts new articles with a link back
to the source.

## Commands

Only the configured owner can interact with the bot; everyone else is ignored.

| Command | What happens |
| --- | --- |
| `/add_term` | Asks for a word or short phrase, analyzes it and adds it to your active card. |
| `/remove_term` | Shows the current card and asks which entry to delete. |
| `/active_word_card` | Prints the active card as text. |
| `/preview_word_card` | Renders the active card and posts the images, without publishing. |
| `/publish_word_card` | Asks for confirmation, then publishes the card (images + audio) to your channel and marks it as published. |
| `/purge_word_card` | Deletes the active, unpublished card. |

New flows are declared in configuration rather than hard-coded into the dispatcher, so the command
set can grow without rewriting the bot core.

## Quick start with Docker

You will need Docker, a Telegram bot token from [@BotFather](https://t.me/BotFather), an OpenAI API
key, and a channel where the bot is allowed to post media.

```bash
git clone https://github.com/AntonSk98/FluentPipe.git
cd FluentPipe
```

Create a `.env` file with your own values:

```bash
cat > .env <<'EOF'
SPRING_DATASOURCE_URL=<your SQLite JDBC URL>
SPRING_AI_OPENAI_API_KEY=<your OpenAI key>
TELEGRAM_BOT_TOKEN=<your bot token>
TELEGRAM_CHANNEL_ID=<your target channel>
TELEGRAM_OWNER_ID=<your numeric Telegram id>
METADATA_STORE_BASE_DIR=<directory for feed history>
EOF
```

Then start the bot:

```bash
docker compose up -d
docker compose logs -f
```

Keep secrets in `.env` (it is git-ignored) and reference the file from compose instead of inlining
tokens:

```yaml
services:
  fluentpipe:
    image: ghcr.io/antonsk98/fluentpipe:latest
    env_file: .env
    volumes:
      - fluentpipe-data:/data
    restart: unless-stopped

volumes:
  fluentpipe-data:
```

Build the image locally instead of pulling it:

```bash
docker build -t fluentpipe:latest .
```

Useful lifecycle commands:

```bash
docker compose stop          # stop, keep state
docker compose down          # remove container/network, keep the data volume
docker compose down -v       # also delete persisted state
```

> Run exactly one instance per bot token. Two pollers cause Telegram `409 Conflict` errors.

## Run locally

Install the JDK version required by the Gradle toolchain (see `build.gradle`). Docker is only needed
if you want to build the container.

Create a `.env` as above, then export it, since Spring Boot does not read `.env` on its own:

```bash
set -a; . ./.env; set +a
./gradlew bootRun
```

Other useful tasks:

```bash
./gradlew test        # offline integration tests; no credentials or network required
./gradlew bootJar     # executable fat JAR in build/libs
./gradlew clean build
```

Tests use their own overrides in `src/test/resources/application.properties` and an importable test
configuration that mocks Telegram, so the suite stays fast and independent of external services.

## Configuration

Required values come from the environment; there are no production defaults.

| Variable | Purpose |
| --- | --- |
| `SPRING_DATASOURCE_URL` | Database connection (SQLite) |
| `SPRING_AI_OPENAI_API_KEY` | OpenAI key for chat and speech |
| `TELEGRAM_BOT_TOKEN` | Bot token from BotFather |
| `TELEGRAM_CHANNEL_ID` | Channel that receives cards |
| `TELEGRAM_OWNER_ID` | Numeric id of the only user allowed to command the bot |
| `METADATA_STORE_BASE_DIR` | Directory for persisted feed history |

Optional feature toggles (all enabled by default):

| Property | Effect |
| --- | --- |
| `fluentpipe.telegram.enabled=false` | Skip Telegram polling and client beans |
| `fluentpipe.news.enabled=false` | Skip RSS flows and bootstrapping |
| `fluentpipe.metadata-store.enabled=false` | Skip the file-backed feed metadata store |

Models, prompts, schedules, feeds and channel settings live in `application.yml` and can be
overridden the usual Spring way, for example through environment variables. Nothing is tied to a
specific provider model or cadence. Change the configuration, not the code.

`fluentpipe.timezone` sets the JVM default timezone at startup, so scheduling and all generated
content share the same clock regardless of the container's default UTC.

## Under the hood (for geeks)

- **Spring Boot + Spring AI** for the orchestration and LLM integration
- **Spring Integration feed adapters** for RSS ingestion, with file-backed feed history
- **Telegram Bots long polling** for updates and publishing
- **SQLite + Spring Data JPA + Flyway** for word cards and schema migrations
- **Thymeleaf + openhtmltopdf + PDFBox** to render HTML to PDF to PNG images
- **Text-to-speech** to generate the card audio
- **Docker multi-stage build** with a slim JRE runtime, non-root user and a persistent `/data` volume

Exact dependency versions are declared in `build.gradle`.

```text
src/main/java/de/ansk98/fluentpipe
├── config/        configuration properties, metadata store, template engine
├── domain/        JPA entities
├── handler/       command/callback SPI, implementations, access filter
├── openai/        AI chat and speech adapter
├── repository/    Spring Data repositories
├── service/       word card service, renderer, RSS bootstrap, document pipes
├── telegram/      long-polling bot, client, mapping utilities
└── FluentPipeApplication.java
src/main/resources
├── application.yml
├── db/migration/  Flyway schema
├── fonts/         fonts embedded into rendered cards
└── templates/     word card template
```

## Container image

CI publishes `ghcr.io/antonsk98/fluentpipe:latest` on every push to the `production` branch. The
container runs as a non-root user and keeps all mutable state under `/data`, so mount a volume
there.

## License

Copyright 2026 AntonSk98.

Licensed under the [Apache License, Version 2.0](LICENSE). You may not use this project except in
compliance with the License. See the [LICENSE](LICENSE) file for the full text.
