# ============================================================
#  PrepTrack AI  –  Production Dockerfile
#  Java 21  |  Maven  |  Spring Boot
# ============================================================

# ── Stage 1: Build ──────────────────────────────────────────
#  We use the official Eclipse Temurin JDK 21 image to compile
#  and package the application into a fat JAR.
# ────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk AS builder

# Set a working directory inside the container for the build
WORKDIR /app

# Copy the Maven wrapper and pom.xml first.
# Docker caches layers — if pom.xml hasn't changed, it won't
# re-download all dependencies on the next build.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Make the Maven wrapper executable (needed on Linux)
RUN chmod +x mvnw

# Download all Maven dependencies (cached layer)
# -B  = batch mode (no interactive prompts)
# -q  = quiet output
# dependency:go-offline = pre-download everything Maven needs
RUN ./mvnw dependency:go-offline -B -q

# Now copy the full source code
COPY src/ src/

# Build the application, skipping tests (tests are for CI/CD pipelines,
# not for building the production image)
# -DskipTests skips test execution but still compiles test sources
RUN ./mvnw package -B -q -DskipTests

# ── Stage 2: Runtime ────────────────────────────────────────
#  We use a smaller JRE-only image for the final container.
#  This keeps the image size small — we don't need the full JDK
#  just to RUN the already-compiled JAR.
# ────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre

# Set the working directory inside the runtime container
WORKDIR /app

# Copy ONLY the built JAR from the builder stage.
# The JAR name comes from pom.xml:
#   artifactId = Fullstack_Project
#   version    = 0.0.1-SNAPSHOT
# Spring Boot Maven plugin produces: Fullstack_Project-0.0.1-SNAPSHOT.jar
COPY --from=builder /app/target/Fullstack_Project-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080.
# Render will override this via the PORT environment variable,
# which is read by: server.port=${PORT:8080}
EXPOSE 8080

# Run the Spring Boot application.
# -Djava.security.egd is a performance tweak that speeds up
# startup on Linux containers by using a faster random source.
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
