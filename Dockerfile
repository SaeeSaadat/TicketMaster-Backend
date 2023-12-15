FROM eclipse-temurin:17-jre

WORKDIR /app

COPY . .

RUN ./mvnw install

COPY target/*.jar .

CMD ["java","-jar","/app.jar"]