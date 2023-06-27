FROM maven:3.8.3-openjdk-17
EXPOSE 8080

# WORKDIR /opt/src
WORKDIR /app
COPY . /app

# COPY . .
RUN /bin/sh /app/build.sh
# Packaging the application
# COPY build.sh /opt/src/scripts/setup.sh
# COPY . .
# RUN mvn clean package
#RUN /bin/sh -c ./build.sh
# ADD build.sh .
# CMD ["/bin/bash", "./build.sh"]
#WORKDIR /tmp
#COPY dist/app.jar /tmp
#CMD ["java", "-jar", "app.jar"]

#VOLUME /tmp
#ARG JAR_FILE
#COPY ${JAR_FILE} app.jar
#ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]

#RUN mvn clean package
#
#
#FROM openjdk:8-jdk-alpine
#VOLUME /tmp
#ARG JAR_FILE
#COPY ${JAR_FILE} app.jar
#ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]