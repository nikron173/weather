FROM maven:3.9.2-eclipse-temurin-17 as BUILD
COPY ./src /data/src
COPY ./pom.xml /data/
RUN mvn -f /data/pom.xml clean package

FROM tomcat:10.1.18-jdk17
COPY --from=BUILD /data/target/weather.war /usr/local/tomcat/webapps/