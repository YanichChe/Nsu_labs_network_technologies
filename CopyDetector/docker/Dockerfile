FROM amazoncorretto:17

ADD ../target/copy-detector-1.0.0-jar-with-dependencies.jar copy-detector-1.0.0-jar-with-dependencies.jar

ENTRYPOINT java -jar copy-detector-1.0.0-jar-with-dependencies.jar -a $GROUP_ADDRESS -p $GROUP_PORT
