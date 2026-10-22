# syntax=docker/dockerfile:1

# ==========================================
# STAGE 1: Build the Application
# ==========================================
# We use a full JDK image for compilation, dependencies, and building the project.
FROM eclipse-temurin:26-jdk-jammy AS builder

# Set the working directory for all subsequent build commands
WORKDIR /workspace

# Copy only the Gradle wrapper and configuration files first.
# WHY: This allows Docker to cache this step. Dependencies won't re-download
# unless your build scripts (build.gradle, settings.gradle) actually change.
COPY gradlew settings.gradle build.gradle ./
COPY gradle/ gradle/

# Ensure the Gradle wrapper script has executable permissions
RUN chmod +x gradlew

# Warm plugin/dependency resolution; cached via BuildKit, only re-runs when build files change
# WHY: "--mount=type=cache" stores the Gradle cache outside of the image layers,
# speeding up subsequent builds while keeping the final image size small.
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --no-daemon help

# Copy the actual source code AFTER dependencies are cached.
# WHY: Code changes frequently; placing this after dependency resolution
# ensures code edits don't invalidate the expensive dependency-download layer.
COPY src/ src/

# Build the executable Spring Boot JAR and isolate it.
# WHY:
# 1. "bootJar" creates an executable fat JAR.
# 2. The "find" command reliably grabs only the runnable application JAR
#    and ignores any layout/plain JARs, copying it to a predictable location.
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --no-daemon bootJar && \
    find build/libs -name '*.jar' ! -name '*-plain.jar' -exec cp {} /workspace/app.jar \;


# ==========================================
# STAGE 2: Runtime Environment
# ==========================================
# We switch to a lightweight JRE image (no compiler needed) to minimize image size and attack surface.
FROM eclipse-temurin:26-jre-jammy AS runtime

# Set the working directory for the runtime application
WORKDIR /app

# Create a secure, non-root system user and prepare local data directories.
# WHY: Running containers as root is a major security risk. Setting up a dedicated
# user (UID 1001) and ensuring it owns the data directory prevents permission errors at runtime.
RUN useradd --system --uid 1001 --create-home app \
    && mkdir -p /data/metadata-store \
    && chown -R app:app /data

# Copy only the compiled application artifact from the builder stage.
# WHY: We leave behind all source code, Gradle caches, and JDK build tools,
# resulting in a lean, secure production image.
COPY --from=builder /workspace/app.jar /app/app.jar

# Switch to the non-root user for all subsequent commands and runtime execution
USER app

# Declare a mount point for external persistent data/volumes
VOLUME ["/data"]

# Define the default command to execute the Java application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]