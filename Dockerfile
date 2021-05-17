FROM adoptopenjdk/openjdk14:jre-14.0.1_7-alpine

WORKDIR /app
EXPOSE 8080

RUN apk add --update --no-cache dumb-init \
    && rm -rf /var/cache/apk/*

COPY build/libs/jwtValidation.jar .

RUN adduser -D runner

USER runner
ENTRYPOINT ["/usr/bin/dumb-init", "--"]
CMD ["java", "-jar", "jwtValidation.jar"]
