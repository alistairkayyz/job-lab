package com.dso34bt.jobportal.controllers;

import com.dso34bt.jobportal.model.Recruiter;
import com.dso34bt.jobportal.model.User;
import com.dso34bt.jobportal.services.RecruiterService;
import com.dso34bt.jobportal.utilities.Validate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class RecruiterController {
    private final RecruiterService recruiterService;

    public RecruiterController(RecruiterService recruiterService) {
        this.recruiterService = recruiterService;
    }

    @GetMapping("recruiter-signup")
    public String signup(Model model) {

        model.addAttribute("success", "");
        model.addAttribute("error", "");
        model.addAttribute("recruiter", new Recruiter());
        return "recruiter-signup";
    }

    @PostMapping("recruiter-signup")
    public String saveRecruiter(@ModelAttribute Recruiter recruiter, Model model) {

        if (recruiterService.existsByEmail(recruiter.getEmail())) {
            model.addAttribute("success", "");
            model.addAttribute("error", "Recruiter with this email already exists.");
            model.addAttribute("recruiter", recruiter);

            return "recruiter-signup";
        }
        if (recruiterService.existsByCompanyName(recruiter.getCompanyName())) {
            model.addAttribute("success", "");
            model.addAttribute("error", "Recruiter with this company name already exists.");
            model.addAttribute("recruiter", recruiter);

            return "recruiter-signup";
        }

        if (!Validate.isUrlValid(recruiter.getWebsiteLink())) {
            model.addAttribute("success", "");
            model.addAttribute("error", "Invalid website URL.");
            model.addAttribute("recruiter", recruiter);

            return "recruiter-signup";
        }

        if (recruiterService.existsByWebsiteLink(recruiter.getWebsiteLink())) {
            model.addAttribute("success", "");
            model.addAttribute("error", "Recruiter with this website link already exists.");
            model.addAttribute("recruiter", recruiter);

            return "recruiter-signup";
        }

        if (!recruiter.getPassword().equals(recruiter.getConfirmPassword())) {
            model.addAttribute("success", "");
            model.addAttribute("error", "Password does not match.");
            model.addAttribute("recruiter", recruiter);

            return "recruiter-signup";
        }

        recruiter.setId(recruiterService.getLastId() + 1);
        recruiter.setRegistrationDate(Timestamp.valueOf(LocalDateTime.now()));

        String success = "";
        String error = "";

        try {
            if (recruiterService.saveRecruiter(recruiter))
                success = "Successfully saved new Recruiter!";
            else
                error = "Failed to save Recruiter!";
        } 
        catch (Exception exception) {
            error = exception.getMessage();
        }

        User user = new User();
        user.setRole("Default");
        
        model.addAttribute("success", success);
        model.addAttribute("error", error);
        model.addAttribute("user", user);
        return "login";
    }

    @GetMapping("recruiter-settings")
    public String settings(Model model,
                           HttpSession session){
        @SuppressWarnings("unchecked")
        List<User> userSessions = (List<User>) session.getAttribute("SESSIONS");

        User user = new User();

        if (userSessions == null) {
            model.addAttribute("user", user);
            model.addAttribute("success", "");
            model.addAttribute("error", "You must login first!");

            return "login";
        }
        else
            user = userSessions.get(userSessions.size() - 1);

        if (!user.getRole().equalsIgnoreCase("recruiter")) {
            model.addAttribute("user", new User());
            model.addAttribute("success", "");
            model.addAttribute("error", "You are not authorized to access this page!");

            return "login";
        }

        String success = "";
        String error = "";

        model.addAttribute("user", user);
        model.addAttribute("recruiter", recruiterService.getRecruiterByEmail(user.getEmail()));
        model.addAttribute("success", success);
        model.addAttribute("error", error);

        return "recruiter-settings";
    }

    @PostMapping("recruiter-settings")
    public String settingsUpdate(@ModelAttribute Recruiter recruiter, Model model,
                           HttpSession session){
        @SuppressWarnings("unchecked")
        List<User> userSessions = (List<User>) session.getAttribute("SESSIONS");

        User user = new User();

        if (userSessions == null) {
            model.addAttribute("user", user);
            model.addAttribute("success", "");
            model.addAttribute("error", "You must login first!");

            return "login";
        }
        else
            user = userSessions.get(userSessions.size() - 1);

        if (!user.getRole().equalsIgnoreCase("recruiter")) {
            model.addAttribute("user", new User());
            model.addAttribute("success", "");
            model.addAttribute("error", "You are not authorized to access this page!");

            return "login";
        }

        String success = "";
        String error = "";

        Recruiter info = recruiterService.getRecruiterByEmail(user.getEmail()).get();

        recruiter.setId(info.getId());
        recruiter.setEmail(info.getEmail());
        recruiter.setPassword(info.getPassword());
        recruiter.setRegistrationDate(info.getRegistrationDate());
        recruiter.setLastLoginDate(info.getLastLoginDate());

        try {
            if (recruiterService.saveRecruiter(recruiter))
                success = "Successfully updated your information.";
            else
                error = "Failed to updated your information";
        }
        catch (Exception e){
            error = e.getMessage();
        }

        model.addAttribute("user", user);
        model.addAttribute("recruiter", recruiterService.getRecruiterByEmail(user.getEmail()));
        model.addAttribute("success", success);
        model.addAttribute("error", error);

        return "recruiter-settings";
    }
}
