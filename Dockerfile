FROM maven:3.9.6-amazoncorretto-21

ENV TZ=America/Sao_Paulo

COPY . .

RUN mvn clean package -DskipTests

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "target/cineapi.jar"]