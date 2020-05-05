FROM openjdk:8-alpine

COPY target/uberjar/yearup.jar /yearup/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/yearup/app.jar"]
