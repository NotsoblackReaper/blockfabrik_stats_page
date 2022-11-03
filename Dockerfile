FROM maven:3.8.4-openjdk-11 AS build
COPY src /usr/src/app/src  
COPY pom.xml /usr/src/app  
RUN mvn -f /usr/src/app/pom.xml clean package

FROM gcr.io/distroless/java  
COPY --from=build /usr/src/app/target/blockfabrik_stats_page-0.1.jar /usr/app/blockfabrik_stats_page-0.1.jar
ENV TZ="Europe/Vienna"
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/app/blockfabrik_stats_page-0.0.2-SNAPSHOT.jar"]