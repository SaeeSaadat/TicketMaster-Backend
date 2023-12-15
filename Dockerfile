FROM eclipse-temurin:17-jre

WORKDIR /app

COPY . .

RUN ./mvnw clean compile
RUN ./mvnw clean install

COPY target/*.jar .

EXPOSE 8080

CMD ["java","-jar","/app.jar"]