FROM amazoncorretto:11-alpine-jdk

COPY build/libs/prolucidToLimelightXML.jar  /usr/local/bin/prolucidToLimelightXML.jar

ENTRYPOINT ["java", "-jar", "/usr/local/bin/prolucidToLimelightXML.jar"]