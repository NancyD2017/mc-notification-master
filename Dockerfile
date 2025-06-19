FROM eclipse-temurin:21-alpine

EXPOSE 8084

ADD /build/libs/mc-notification-0.0.1-SNAPSHOT.jar mc-notification.jar

ENTRYPOINT ["java", "-jar", "mc-notification.jar"]