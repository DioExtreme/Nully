FROM eclipse-temurin:17-jre-alpine

RUN apk add --no-cache font-ipa font-noto-emoji && rm -rf /var/cache/apk/*

RUN addgroup -S nully && adduser -S nully -G nully
USER nully:nully

ARG JAR_FILE

COPY ${JAR_FILE} nully.jar

ENTRYPOINT ["java", "-XX:+UseZGC", "-jar", "nully.jar"]
