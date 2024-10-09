# Используем официальный образ OpenJDK в качестве основы
FROM openjdk:17-jdk-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем jar-файл приложения в контейнер
COPY target/Lab1-1.1.jar /app/app.jar

# Устанавливаем переменную среды, указывающую, что это Spring Boot приложение
ENV JAVA_OPTS=""

# Указываем команду запуска
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]

# Указываем, что приложение использует порт 8080
EXPOSE 8080