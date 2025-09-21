# Quizzy


Quizzy is a dynamic and intelligent quiz application built with a scalable microservices architecture. It allows users to create, take, and manage quizzes on various topics. The application features a conventional quiz creation method from a pre-populated question bank and an innovative AI-powered quiz generation feature using Google's Gemini Pro.

## Architecture

The application is composed of four main microservices, orchestrated to provide a seamless quizzing experience:

*   **Service Registry (`service-registry`)**: A Eureka server that allows other services to register themselves and discover each other. It acts as the switchboard for the entire system.
*   **Question Service (`question-service`)**: Manages the question bank. It handles creating, reading, and updating questions in a PostgreSQL database. It also provides endpoints to generate a random set of question IDs for a quiz and to calculate scores.
*   **Quiz Service (`quiz-service`)**: Responsible for quiz creation and lifecycle management. It communicates with the `Question Service` via OpenFeign to fetch question details. This service also integrates with Google's Gemini AI to generate tailored questions based on user-defined criteria like topic, difficulty, and professional role.
*   **API Gateway (`api-gateway`)**: The single entry point for all client requests. It uses Spring Cloud Gateway to route traffic to the appropriate microservices, simplifying the client-side logic and enhancing security.


*(Note: Visual representation of service interaction)*

1.  All services (`quiz-service`, `question-service`, `api-gateway`) register with the `service-registry`.
2.  All external client requests are sent to the `api-gateway`.
3.  The `api-gateway` routes requests to either the `quiz-service` or `question-service`.
4.  The `quiz-service` communicates with the `question-service` to generate quizzes and calculate results.
5.  For AI-powered quiz generation, `quiz-service` calls the Google Gemini AI API.

## Features

*   **Microservices Architecture**: Scalable and resilient design using Spring Boot & Spring Cloud.
*   **Service Discovery**: Eureka for dynamic service registration and discovery.
*   **Centralized Routing**: API Gateway for unified access to all services.
*   **Manual Quiz Creation**: Create quizzes by specifying a category and the number of questions.
*   **AI-Powered Quiz Generation**: Generate personalized quizzes using Google Gemini, tailored to a specific topic, difficulty level, role, and years of experience.
*   **Quiz Participation**: Take quizzes and submit answers.
*   **Automated Scoring**: Get instant results upon quiz submission.
*   **Database Integration**: Uses PostgreSQL for persistent storage of questions and quiz data.

## Technologies Used

*   **Backend**: Java 17, Spring Boot 3
*   **Spring Cloud**:
    *   Spring Cloud Gateway
    *   Spring Cloud Netflix Eureka (Service Discovery)
    *   Spring Cloud OpenFeign (Declarative REST Client)
*   **Database**: PostgreSQL
*   **AI Integration**: Google Cloud AI Platform (Gemini Pro)
*   **Build Tool**: Maven
*   **Data Handling**: Spring Data JPA, Lombok

## Getting Started

### Prerequisites

*   Java (JDK 17)
*   Apache Maven
*   PostgreSQL Server
*   A Google Cloud account with a project ID and API key for the AI features.

### 1. Database Setup

You need to create two separate databases in PostgreSQL:

1.  `questiondb`: For the `question-service`.
2.  `quizdb`: For the `quiz-service`.

After creating `questiondb`, run the `question-table-data.sql` script to create the `question` table and populate it with initial sample data.

```sql
-- Connect to 'questiondb' and run the contents of:
-- question-table-data.sql
```

### 2. Configuration

You must configure database credentials and API keys for the services to run correctly.

**For `question-service` (in `question-service/src/main/resources/application.properties`):**
Set your PostgreSQL password. It's recommended to use environment variables.
```properties
spring.datasource.password=${DB_PASSWORD}
```

**For `quiz-service` (in `quiz-service/src/main/resources/application.properties`):**
Set your PostgreSQL password and your Google Gemini API credentials.
```properties
# Google Cloud/Gemini AI Configuration
gemini.api.key=${API_KEY}
gemini.project.id=${PROJECT_ID}

# Database Configuration
spring.datasource.password=${DB_PASSWORD}
```

You can set these `DB_PASSWORD`, `API_KEY`, and `PROJECT_ID` as environment variables in your operating system or IDE's run configuration.

### 3. Running the Application

Start the microservices in the following order. Open a new terminal for each service.

1.  **Service Registry**
    ```bash
    cd service-registry
    ./mvnw spring-boot:run
    ```
    Wait for it to start. You can view the Eureka dashboard at `http://localhost:8761`.

2.  **Question Service**
    ```bash
    cd question-service
    ./mvnw spring-boot:run
    ```

3.  **Quiz Service**
    ```bash
    cd quiz-service
    ./mvnw spring-boot:run
    ```

4.  **API Gateway**
    ```bash
    cd api-gateway
    ./mvnw spring-boot:run
    ```
The API gateway runs on `http://localhost:8765`. All subsequent API calls should be made through this gateway.

## API Endpoints

All endpoints are accessed through the API Gateway at `http://localhost:8765`.

### Question Service (`/question`)

*   **Get All Questions**
    *   `GET /question/allQuestions`

*   **Get Questions by Category**
    *   `GET /question/category/{category}`

*   **Add a New Question**
    *   `POST /question/add`
    *   Body:
        ```json
        {
            "questionTitle": "Which loop in Java allows the code to be executed at least once?",
            "option1": "for loop",
            "option2": "while loop",
            "option3": "do-while loop",
            "option4": "switch loop",
            "rightAnswer": "do-while loop",
            "difficultylevel": "Easy",
            "category": "Java"
        }
        ```

### Quiz Service (`/quiz`)

*   **Create a Quiz from the Question Bank**
    *   `POST /quiz/create`
    *   Body:
        ```json
        {
            "categoryName": "Java",
            "numQuestions": 5,
            "title": "Java Basics Quiz"
        }
        ```

*   **Create a Quiz using AI**
    *   `POST /quiz/generate-with-ai`
    *   Body:
        ```json
        {
            "category": "Python",
            "difficultyLevel": "EASY",
            "roleType": "PYTHON_DEVELOPER",
            "yearsOfExperience": 2,
            "numQuestions": 5,
            "quizTitle": "AI Python Quiz"
        }
        ```

*   **Get Quiz Questions to Take a Quiz**
    *   `POST /quiz/get/{id}`

*   **Submit a Quiz and Get the Score**
    *   `POST /quiz/submit/{id}`
    *   Body:
        ```json
        [
            {
                "id": 1,
                "response": "extends"
            },
            {
                "id": 2,
                "response": "5"
            }
        ]
