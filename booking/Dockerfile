FROM adoptopenjdk/openjdk11:alpine-jre
RUN mkdir /opt/app
COPY ./target/booking-0.jar /opt/app
EXPOSE 8084
CMD ["java", "-Dspring.profiles.active=kubernetes,security-kubernetes", "-jar", "/opt/app/booking-0.jar"]
