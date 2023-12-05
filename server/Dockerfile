FROM openjdk:11

EXPOSE 8080:8080

RUN mkdir /app

COPY ./src/main/resources/calendarapp346-db9002f5232a.json /app/calendarapp346-db9002f5232a.json

COPY ./build/libs/*-all.jar /app/calendarApp.jar
ENTRYPOINT ["java","-jar","/app/calendarApp.jar"]