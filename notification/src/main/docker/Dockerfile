###
# vert.x docker example using a Java verticle
# To build:
#  docker build -t notification .
# To run:
#   docker run -t -i --rm -p 8085:8085 notification
###

# Extend vert.x image
FROM adoptopenjdk/openjdk11:alpine-jre

ENV VERTICLE_FILE ./target/notification-0-fat.jar
ENV CONFIGURATION_FILE_NAME ./configuration-kubernetes.json

# Set the location of the verticles
ENV VERTICLE_HOME /opt/application

EXPOSE 8085

# Copy your verticle to the container
COPY ./verticles $VERTICLE_HOME
COPY ./configuration/configuration-kubernetes.json $VERTICLE_HOME/configuration.json

# Launch the verticle
WORKDIR "/opt/application/"
CMD ["java", "-jar", "/opt/application/notification-0-fat.jar"]
