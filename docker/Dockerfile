FROM maven:3.9.0 AS BUILD
COPY src /home/tribe/src
COPY pom.xml /home/tribe
RUN --mount=type=cache,target=/root/.m2 mvn -f /home/tribe/pom.xml clean package -DskipTests

FROM openjdk:17-jdk
COPY --from=build /home/tribe/target/tribe.jar /usr/local/lib/tribe.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/usr/local/lib/tribe.jar"]