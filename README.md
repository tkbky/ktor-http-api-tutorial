# Ktor HTTP API Tutorial

Learn Ktor to build a HTTP API by following [this tutorial](https://ktor.io/docs/creating-http-apis.html).

## Usage

```sh
# Create a customer
curl -i -X POST -H "Content-Type: application/json" -d '{"id": 1, "email": "foo@example.com", "firstName": "foo", "lastName": "the"}' http://localhost:8080/customer
# List all customers
curl -i http://localhost:8080/customer
# Get a customer
curl -i http://localhost:8080/customer/1
# Delete a customer
curl -i -X DELETE http://localhost:8080/customer/1

# Basic auth
curl -i -u admin:admin http://localhost:8080/auth/basic

# JWT auth
# Get JWT token
curl -i -X POST -H "Content-Type: application/json" -d '{"username":"admin","password":"admin"}' http://localhost:8080/auth/jwt-login
# Access JWT authenticated endpoint
curl -i -H "Authorization: Bearer <jwt-token>" http://localhost:8080/auth/jwt
```

## Docker

```sh
./gradlew installDist

docker build -t ktor-http-api-tutorial .

docker run -p 8080:8080 ktor-http-api-tutorial
```

## Database

```sh
psql postgres

create role root login password 'root'
 
# Test connection
psql -U root -W postgres
postgres=> \q

# Create database for development & test
psql postgres
create database ktor_http_api_tutorial_development;
create database ktor_http_api_tutorial_test;
```