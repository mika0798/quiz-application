# 1. Introduction
Simple in-memory quiz application built with Spring Boot and Thymeleaf. Stores users and questions in memory; suitable for learning and prototyping.

![screenshots](screenshots/cutclip.gif)
# 2. Role
Role: Backend developer  
Responsibilities: implement controllers, services, security configuration, and in-memory data management for users and questions.

# 3. Technologies used
- Java (Spring Boot)
- Spring Security
- Thymeleaf templates
- Maven

Key backend classes:
- com.project.quiz_application.QuizApplicationInmemoryApplication
- com.project.quiz_application.controller.QuizAppController
- com.project.quiz_application.service.QuestionService
- com.project.quiz_application.service.QuizUserDetailsService
- com.project.quiz_application.config.WebSecurityConfig

# 4. Request flow
1. Client -> /register or /login (public endpoints). Registration creates an in-memory user.
2. Authentication handled by QuizUserDetailsService and WebSecurityConfig (session-based).
3. After login:
    - ROLE_ADMIN: access admin UI and endpoints
        - GET /quizlist (list all questions)
        - GET/POST /addquiz (create question)
        - GET/POST /editquiz (update question)
        - POST /deletequiz (remove question)
    - ROLE_USER: access quiz-taking endpoints
        - GET /quiz (take quiz)
        - POST /result (submit answers and get score)
4. Controller layer (QuizAppController) receives requests, calls QuestionService for question CRUD and business logic.
5. QuestionService manages in-memory question storage and returns data to controller.
6. Controller renders Thymeleaf templates or redirects as needed.
7. WebSecurityConfig restricts endpoints by role and handles login/logout.