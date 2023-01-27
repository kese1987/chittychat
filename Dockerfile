FROM openjdk:8
ARG JAR
WORKDIR /
COPY chittychat-${JAR}.jar chittychat.jar
ENTRYPOINT ["java","-jar","chittychat.jar"]