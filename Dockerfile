FROM openjdk:8
ARG JAR
WORKDIR /
COPY chittychat-${JAR}.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]