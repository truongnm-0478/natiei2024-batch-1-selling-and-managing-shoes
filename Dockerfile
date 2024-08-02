FROM openjdk:17
VOLUME /tmp
COPY target/*.war app.war
ENTRYPOINT ["java","-jar","/app.war"]
