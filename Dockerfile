
FROM tomcat:10.1-jdk17-temurin

# Metadata
LABEL maintainer="Eranda"
LABEL description="Bus Reservation System - Enactor Technical Test"

RUN rm -rf /usr/local/tomcat/webapps/*
COPY build/libs/*.war /usr/local/tomcat/webapps/ROOT.war

# Expose port 8080
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]