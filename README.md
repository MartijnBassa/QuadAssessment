# QuadAssessment — Trivia Quiz App

A full-stack trivia quiz application. Configure and play a quiz in the browser; the backend fetches questions from the [Open Trivia Database](https://opentdb.com/) and validates answers server-side so correct answers are never sent to the client.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | Vue 3, Vite |
| Backend | Spring Boot 4, Java 26 |
| Testing | Vitest (frontend), JUnit 5 + Mockito (backend) |

---

## Prerequisites

| Tool | Version |
|------|---------|
| Java | 26 |
| Node.js | 20.19+ or 22.12+ |

---

## Running the Application

**1. Start the backend**

```bash
cd backend
./gradlew bootRun
```

API available at `http://localhost:8080`.

**2. Start the frontend**

```bash
cd frontend
npm install
npm run dev
```

App available at `http://localhost:5173`.

---

## Running Tests

**Backend**
```bash
cd backend
./gradlew test
```

**Frontend**
```bash
cd frontend
npm run test
```

---

## Project Structure

```
QuadAssessment/
├── backend/
│   └── src/
│       ├── main/java/com/quad/trivia/backend/
│       │   ├── BackendApplication.java
│       │   ├── CorsConfig.java
│       │   ├── client/
│       │   │   └── OpenTriviaClient.java        fetches from Open Trivia DB
│       │   ├── controller/
│       │   │   └── TriviaController.java        GET /api/questions, POST /api/checkanswers
│       │   ├── dto/
│       │   │   ├── external/                    API response from Open Trivia DB
│       │   │   ├── request/                     SubmitAnswersRequest, QuestionDto
│       │   │   └── response/                    QuizResponse, CheckAnswersResponse, QuizDisplayDto, AnswerResult.
│       │   ├── exception/
│       │   │   ├── GlobalExceptionHandler.java
│       │   │   ├── TriviaErrorCode.java
│       │   │   └── TriviaException.java
│       │   └── service/
│       │       └── TriviaService.java           quiz logic, answer storage
│       └── test/java/com/quad/trivia/backend/
│           └── TriviaService.java               Trivia service unit tests
│
└── frontend/
    └── src/
        ├── App.vue
        ├── main.js
        ├── trivia-options.json                  category/difficulty/type dropdown data
        ├── __tests__/
        │   └── triviaForm.spec.js
        ├── components/
        │   └── triviaForm.vue
        └── css/
            └── triviaForm.css
```

---

## API

### `GET /api/questions`

Fetches a new quiz and stores the correct answers server-side.

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `amount` | int | yes | Number of questions (1–50) |
| `category` | int | no | Category ID |
| `difficulty` | string | no | `easy`, `medium`, or `hard` |
| `type` | string | no | `multiple` or `boolean` |

**Response** — correct answers are not included:

```json
{
  "quizId": "uuid",
  "results": [
    {
      "category": "General Knowledge",
      "question": "When did the website \"Facebook\" launch?",
      "options": ["2006", "2005", "2003", "2004"],
      "difficulty": "medium",
      "type": "multiple"
    }
  ]
}
```
#### Example Requests

```http
GET http://localhost:8080/api/questions?amount=10
```

```http
GET http://localhost:8080/api/questions?amount=10&category=10
```

---

### `POST /api/checkanswers`

Checks submitted answers against the correct answers on the server.

**Request body:**

```json
{
  "quizId": "uuid",
  "answers": { "0": "2004", "1": "False" }
}
```

**Response:**

```json
{
  "results": [
    { "questionIndex": 0, "correct": true,  "correctAnswer": "2004" },
    { "questionIndex": 1, "correct": false, "correctAnswer": "False" }
  ],
  "score": 1
}
```
