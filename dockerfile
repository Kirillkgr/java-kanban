FROM maven:3.9.8-amazoncorretto-21-al2023 AS builder
WORKDIR /JavaKanban
COPY .  .
RUN mvn clean package

FROM openjdk:21-slim
WORKDIR /JavaKanban
EXPOSE 8080
COPY --from=builder /JavaKanban/target/*.jar ./JavaKanban.jar
CMD ["java", "-jar", "JavaKanban.jar"]