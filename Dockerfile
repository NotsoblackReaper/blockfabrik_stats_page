FROM maven:3.8.4-openjdk-11 AS build
COPY src /usr/src/app/src  
COPY pom.xml /usr/src/app  
RUN mvn -f /usr/src/app/pom.xml clean package

FROM gcr.io/distroless/java  
COPY --from=build /usr/src/app/target/blockfabrik_stats_page-0.2.jar /usr/app/blockfabrik_stats_page-0.2.jar
ENV TZ="Europe/Vienna"
ENV DATA_SCRAPING=${data_scraping}
ENV WEB_SERVER=${web_server}
ENV TF_SUPPORT=${tf_support}
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/app/blockfabrik_stats_page-0.2.jar"]