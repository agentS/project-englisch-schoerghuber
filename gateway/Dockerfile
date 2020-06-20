FROM adoptopenjdk/openjdk11:alpine
RUN mkdir /opt/app
COPY ./target/gateway-0.jar /opt/app
EXPOSE 8081
CMD ["java", "-Dspring.profiles.active=kubernetes,security-kubernetes", "-jar", "/opt/app/gateway-0.jar"]
