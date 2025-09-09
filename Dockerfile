FROM tomcat:9.0-jdk11
COPY target/student-registration.war /usr/local/tomcat/webapps/student-registration.war