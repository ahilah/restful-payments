FROM adoptopenjdk/openjdk17:alpine

# директорія робочої зони в контейнері
WORKDIR /app

# JAR-файл у директорію /app у контейнері
COPY target/rest-payments-0.0.1-SNAPSHOT.jar /app/rest-payments.jar

CMD ["java", "-jar", "rest-payments.jar"]