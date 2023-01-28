FROM openjdk:17
ARG JAR
ARG APP_NAME
WORKDIR /
COPY ${APP_NAME}-${JAR}.jar ${APP_NAME}.jar
ENTRYPOINT ["java","-jar","chittychat.jar"]