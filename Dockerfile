FROM openjdk:8-jdk
EXPOSE 8080:8080
RUN mkdir /app
COPY ./build/install/com.example.ktor-http-api-tutorial/ /app/
WORKDIR /app/bin
CMD ["./com.example.ktor-http-api-tutorial"]