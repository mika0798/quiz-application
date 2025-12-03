package com.project.quiz_application.controller;

import com.project.quiz_application.model.Question;
import com.project.quiz_application.model.User;
import com.project.quiz_application.service.QuestionService;
import com.project.quiz_application.service.QuizUserDetailsService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
public class QuizAppController {
    private final AuthenticationManager authenticationManager;
    private final QuizUserDetailsService userDetailsService;
    private final QuestionService questionService;

    @Autowired
    public QuizAppController(AuthenticationManager authenticationManager, QuizUserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.questionService = new QuestionService();
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
            return "redirect:login";
        }
        String username = auth.getName();
        model.addAttribute("username", username);

        String role = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER"); // Set default if service cannot find any authority

        if (role.equals("ROLE_ADMIN")) {
            List<Question> questions = questionService.getQuizzesList();
            model.addAttribute("quizzes", questions);
            return "quizlist";
        } else {
            List<Question> questions = questionService.getQuizzesList();
            model.addAttribute("quizzes", questions);
            return "quiz";
        }
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid User registerUser,
            BindingResult bindingResult
    ) {
        try {
            if (bindingResult.hasErrors()) {
                return "register";
            }

            userDetailsService.registerUser(registerUser);
        } catch (Exception userAlreadyExists) {
            return "redirect:/register?error";
        }

        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(registerUser.getUsername(), registerUser.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        return  "redirect:/login?success";

    }

    /* There's no need to write POST API for login service,
    security filter chain already takes care of that :))
     */
}
