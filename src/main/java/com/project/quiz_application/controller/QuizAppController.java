package com.project.quiz_application.controller;

import com.project.quiz_application.model.Question;
import com.project.quiz_application.model.User;
import com.project.quiz_application.service.QuestionService;
import com.project.quiz_application.service.QuizUserDetailsService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class QuizAppController {
    private final AuthenticationManager authenticationManager;
    private final QuizUserDetailsService userDetailsService;
    private final QuestionService questionService;

    @PostConstruct
    public void init() throws Exception {
        Question question1 = new Question(
                questionService.getNextId(),
                "What is the first letter in the alphabet?",
                new ArrayList<>(Arrays.asList("D","A","Y","W")),
                "A"
                );

        Question question2 = new Question(
                questionService.getNextId(),
                "What year was Hitler born in?",
                new ArrayList<>(Arrays.asList("1898","1881","1889","1890")),
                "1889"
        );
        questionService.addQuiz(question1);
        questionService.addQuiz(question2);
        userDetailsService.registerUser(new User("admin","123","admin@email.com","ADMIN"));
        userDetailsService.registerUser(new User("user","123","user@gmail.com","USER"));
    }

    @Autowired
    public QuizAppController(AuthenticationManager authenticationManager, QuizUserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.questionService = new QuestionService();
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/home";
    }

    /*---- Login and Register API ----*/

    @GetMapping("/login")
    public String login() {return "login";}

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("newUser", new User());
        return "register";
    }

    @GetMapping("/home")
    public String home(Model model) {
        Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }
        String username = auth.getName();
        model.addAttribute("username", username);

        String role = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER"); // Set default if service cannot find any authority

        if (role.equals("ROLE_ADMIN")) {
            List<Question> questions = questionService.getQuizzesList();
            model.addAttribute("questions", questions);
            return "quizlist";
        } else {
            List<Question> questions = questionService.getQuizzesList();
            model.addAttribute("questions", questions);
            return "quiz";
        }
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("newUser") User registerUser,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        String rawPassword = registerUser.getPassword();
        try {
            userDetailsService.registerUser(registerUser);
        } catch (Exception userAlreadyExists) {
            return "redirect:/register?error";
        }

        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(registerUser.getUsername(), rawPassword));

        SecurityContextHolder.getContext().setAuthentication(auth);
        return  "redirect:/login?success";
    }

    /* There's no need to write POST API for login service,
    security filter chain already takes care of that :))
     */
    /*---- Adding Quiz GET, POST APIs ----*/

    /*---- Adding Quiz GET, POST APIs ----*/
    @GetMapping("/addquiz")
    public String showAddQuiz(Model model) {
        model.addAttribute("question",new Question());
        return "addquiz";
    }

    @PostMapping("/addquiz")
    public String addQuiz(
            @ModelAttribute @Valid Question question,
            BindingResult bindingResult,
            Model model,
            Authentication auth) {
        if (bindingResult.hasErrors()) {
            return "addquiz";
        }

        String role = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");
        if (role.equals("ROLE_ADMIN")) {
            question.setId(questionService.getNextId());
            if (questionService.addQuiz(question)) {
                model.addAttribute("success","Added question successfully!");
                return "redirect:/addquiz?success";
            } else {
                model.addAttribute("error","Failed to add question!");
                return "redirect:/addquiz?error";
            }

        } else {
            model.addAttribute("error","You do not have permission to do this!");
            return "redirect:/home";
        }
    }

    /*---- Editing Quiz GET, POST APIs ----*/
    @GetMapping("/editquiz/{id}")
    public String editQuiz(@PathVariable("id") int id, Model model) {
        model.addAttribute("question",questionService.getQuizById(id));
        return "editquiz";
    }

    @PostMapping("/editquiz")
    public String editQuiz(
            @ModelAttribute("question") @Valid Question question,
            BindingResult bindingResult,
            Model model,
            Authentication auth
    ) {
        if (bindingResult.hasErrors()) {
            return "editquiz";
        }

        String role = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");
        if (role.equals("ROLE_ADMIN")) {
            if (questionService.editQuiz(question)) {
                model.addAttribute("success","Edited question successfully!");
                return "redirect:/home?success";
            } else {
                model.addAttribute("error","Cannot edit question!");
                return "redirect:/edit/{id}?error";
            }

        } else  {
            model.addAttribute("error","You do not have permission to do this!");
            return "redirect:/home";
        }
    }


    /*---- Deleting Quiz API ----*/

    /*---- Submitting API ----*/

}
