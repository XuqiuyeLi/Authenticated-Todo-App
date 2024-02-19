
# REST CRUD Authentication API for a todo app
## Technologies
* Spring Boot
* Spring Security
* Kotlin
* PostgreSQL
* JPA and Hibernate
* JWT (JSON Web Token)
* AWS Elastic Beanstalk
* AWS RDS

## Getting Started
To get started with this project, you will need to have the following installed on your local machine:

* JDK 17+
* PostgreSQL 14+

**1. Create Local PostgreSQL database**
```bash
create database todo-dev
create database todo-dev-test
```

**2. Change postgreSQL username and password as per your installation**

+ open `src/main/resources/application-dev.yml`

+ change `spring.datasource.username` and `spring.datasource.password` as per your postgreSQL installation

**3. Add local environment variables**
+ make sure application-dev.yml is configured when starting the app locally
    ```bash
    export SPRING_PROFILES_ACTIVE=dev 
    ```
+ you need to create a JWT secret key at least 256 bits on your own, if you have access to a Unix/Linux command line, you can generate a secret key using `openssl`
    ```bash
    openssl rand -base64 64
    ```
+ add jwt secret key generated to your environment variables
    ```bash
    export JWT_KEY=<your-jwt-secret-key>
    ```
**4. Build and run the app using Makefile**

```bash
make start
```

The app will start running at <http://localhost:8080>.

## Explore Rest APIs
The app defines following authentication APIs which users can access without JWT token.
    
    POST /api/v1/auth/register
    
    POST /api/v1/auth/authenticate

The app defines following CRUD APIs after the user is authenticated.
    
    GET /api/v1/todos
    
    POST /api/v1/todos
    
    GET /api/v1/todos/{id}
    
    PATCH /api/v1/todos/{id}
    
    DELETE /api/v1/todos/{id}

