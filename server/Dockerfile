FROM openjdk:14
EXPOSE 8283/tcp
WORKDIR /app
COPY ./target/scala-2.13/ScalaTinkoffInvestServer-assembly-0.1.jar /app/ScalaTinkoffInvestServer-assembly-0.1.jar
CMD java -jar \/app\/ScalaTinkoffInvestServer-assembly-0.1.jar -server -Xmx2144 -XX:+UseParallelGC