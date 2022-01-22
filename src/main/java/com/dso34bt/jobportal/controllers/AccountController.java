package com.dso34bt.jobportal.controllers;

import com.dso34bt.jobportal.model.CandidateAccount;
import com.dso34bt.jobportal.model.Recruiter;
import com.dso34bt.jobportal.model.User;
import com.dso34bt.jobportal.services.CandidateAccountService;
import com.dso34bt.jobportal.services.JobPostService;
import com.dso34bt.jobportal.services.RecruiterService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AccountController {
    private final CandidateAccountService candidateAccountService;
    private final RecruiterService recruiterService;
    private final JobPostService jobPostService;

    public AccountController(CandidateAccountService candidateAccountService,
                             RecruiterService recruiterService, JobPostService jobPostService) {
        this.candidateAccountService = candidateAccountService;
        this.recruiterService = recruiterService;
        this.jobPostService = jobPostService;
    }

    @GetMapping("login")
    public String candidateLogin(Model model, HttpSession session) {
        @SuppressWarnings("unchecked")
        List<User> userSessions = (List<User>) session.getAttribute("SESSIONS");

        if (userSessions == null) {
            model.addAttribute("user", new User());
        }
        else
            model.addAttribute("user", userSessions.get(userSessions.size() - 1));

        model.addAttribute("success", "");
        model.addAttribute("error", "");

        return "login";
    }

    @PostMapping("login")
    public String verifyCandidate(@ModelAttribute User user, Model model, BindingResult bindingResult,
                                  HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("success", "");
            model.addAttribute("error", "");

            return "login";
        }

        CandidateAccount candidateAccount;
        Recruiter recruiter;

        if (user.getRole().equalsIgnoreCase("candidate")){
            if (!candidateAccountService.existsByEmail(user.getEmail())) {
                model.addAttribute("user", user);
                model.addAttribute("success", "");
                model.addAttribute("error", "Invalid username or password!");

                return "login";
            }
            candidateAccount = candidateAccountService.getUserAccountByEmail(user.getEmail()).get();

            if (!candidateAccount.getPassword().equals(user.getPassword())) {
                model.addAttribute("user", user);
                model.addAttribute("success", "");
                model.addAttribute("error", "Invalid username or password!");

                return "login";
            }

            candidateAccount.setLastLoginDate(Timestamp.valueOf(LocalDateTime.now()));

            if (!candidateAccountService.saveCandidateAccount(candidateAccount)){
                model.addAttribute("user", user);
                model.addAttribute("success", "");
                model.addAttribute("error", "Failed to update user details");

                return "login";
            }
            else {
                @SuppressWarnings("unchecked")
                List<User> userSessions = (List<User>) request.getSession().getAttribute("MY_SESSION_MESSAGES");

                if (userSessions == null) {
                    userSessions = new ArrayList<>();
                    request.getSession().setAttribute("SESSIONS", userSessions);
                }

                userSessions.add(user); // add new session

                request.getSession().setAttribute("SESSIONS", userSessions);

                model.addAttribute("user", userSessions.get(userSessions.size() - 1));
                model.addAttribute("jobPosts", jobPostService.getJobPosts());
                return "redirect:/index";
            }

        }

        if (user.getRole().equalsIgnoreCase("recruiter")){
            if (!recruiterService.existsByEmail(user.getEmail())) {
                model.addAttribute("user", user);
                model.addAttribute("success", "");
                model.addAttribute("error", "Invalid username or password!");

                return "login";
            }

            recruiter = recruiterService.getRecruiterByEmail(user.getEmail()).get();

            if (!recruiter.getPassword().equals(user.getPassword())) {
                model.addAttribute("user", user);
                model.addAttribute("success", "");
                model.addAttribute("error", "Invalid username or password!");

                return "login";
            }

            recruiter.setLastLoginDate(Timestamp.valueOf(LocalDateTime.now()));

            if (!recruiterService.saveRecruiter(recruiter)){
                model.addAttribute("user", user);
                model.addAttribute("success", "");
                model.addAttribute("error", "Failed to update user details");

                return "login";
            }
            else {
                @SuppressWarnings("unchecked")
                List<User> userSessions = (List<User>) request.getSession().getAttribute("MY_SESSION_MESSAGES");

                if (userSessions == null) {
                    userSessions = new ArrayList<>();
                    request.getSession().setAttribute("SESSIONS", userSessions);
                }

                userSessions.add(user); // add new session

                request.getSession().setAttribute("SESSIONS", userSessions);

                model.addAttribute("user", userSessions.get(userSessions.size() - 1));
                model.addAttribute("jobs", jobPostService.getJobPosts());
                return "redirect:/view-jobs";
            }
        }
        model.addAttribute("user", user);
        model.addAttribute("success", "");
        model.addAttribute("error", "Something went wrong! Try again later");

        return "login";
    }

    @GetMapping("signup")
    public String candidateSignup(Model model) {
        model.addAttribute("candidate", new CandidateAccount());
        model.addAttribute("success", "");
        model.addAttribute("error", "");

        return "signup";
    }

    @PostMapping("signup")
    public String saveCandidate(@ModelAttribute CandidateAccount candidate,
                                  Model model, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            model.addAttribute("candidate", new CandidateAccount());
            model.addAttribute("success", "");
            model.addAttribute("error", "Invalid username or password!");

            return "signup";
        }

        if (candidateAccountService.existsByEmail(candidate.getEmail())) {
            model.addAttribute("candidate", new CandidateAccount());
            model.addAttribute("success", "");
            model.addAttribute("error", "Username already taken!");

            return "signup";
        }

        if (!candidate.getPassword().equals(candidate.getConfirmPassword())) {
            model.addAttribute("candidate", new CandidateAccount());
            model.addAttribute("success", "");
            model.addAttribute("error", "Password does not match!");

            return "signup";
        }

        candidate.setId(candidateAccountService.getLastId() + 1);
        candidate.setEmailNotificationActive(false);
        candidate.setRegistrationDate(Timestamp.valueOf(LocalDateTime.now()));
        candidate.setLastLoginDate(Timestamp.valueOf(LocalDateTime.now()));

        if (!candidateAccountService.saveCandidateAccount(candidate)){
            model.addAttribute("candidate", new CandidateAccount());
            model.addAttribute("success", "");
            model.addAttribute("error", "Failed to save user, try again later!");

            return "signup";
        }

        User user = new User();
        user.setRole("Default");

        model.addAttribute("user", user);
        model.addAttribute("success", "You've successfully signed up!");
        model.addAttribute("error", "");

        return "login";
    }

    @GetMapping("forgot-password")
    public String changePassword(Model model) {
        User user = new User();
        user.setRole("Default");

        model.addAttribute("user", user);
        model.addAttribute("success", "");
        model.addAttribute("error", "");

        return "forgot-password";
    }

    @PostMapping("forgot-password")
    public String newPassword(@ModelAttribute User user,
                                           Model model, BindingResult bindingResult){
        CandidateAccount candidateAccount;
        Recruiter recruiter;

        if (!user.getPassword().equals(user.getConfirmPassword())) {
            model.addAttribute("user", user);
            model.addAttribute("success", "");
            model.addAttribute("error", "Password does not match!");

            return "forgot-password";
        }

        if (user.getRole().equalsIgnoreCase("candidate")){
            if (!candidateAccountService.existsByEmail(user.getEmail())) {
                model.addAttribute("user", user);
                model.addAttribute("success", "");
                model.addAttribute("error", "User not found!");

                return "forgot-password";
            }
            candidateAccount = candidateAccountService.getUserAccountByEmail(user.getEmail()).get();

            if (!candidateAccountService.saveCandidateAccount(candidateAccount)){
                model.addAttribute("user", user);
                model.addAttribute("success", "");
                model.addAttribute("error", "Failed to update password");

                return "forgot-password";
            }

        }

        if (user.getRole().equalsIgnoreCase("recruiter")){
            if (!recruiterService.existsByEmail(user.getEmail())) {
                model.addAttribute("user", user);
                model.addAttribute("success", "");
                model.addAttribute("error", "User not found!");

                return "forgot-password";
            }

            recruiter = recruiterService.getRecruiterByEmail(user.getEmail()).get();

            if (!recruiterService.saveRecruiter(recruiter)){
                model.addAttribute("user", user);
                model.addAttribute("success", "");
                model.addAttribute("error", "Failed to update password");

                return "forgot-password";
            }
        }
        model.addAttribute("user", user);
        model.addAttribute("success", "Successfully changed password!");
        model.addAttribute("error", "");

        return "login";
    }
}
