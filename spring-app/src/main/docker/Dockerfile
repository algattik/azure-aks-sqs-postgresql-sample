FROM mcr.microsoft.com/java/jre-headless:11u5-zulu-alpine	
COPY sqs-to-postgresql-0.0.1-SNAPSHOT.jar app.jar
ENV JAVA_OPTS=""
ENTRYPOINT exec java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=9999 -jar /app.jar
