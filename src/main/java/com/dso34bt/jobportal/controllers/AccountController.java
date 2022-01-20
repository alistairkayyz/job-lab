package com.dso34bt.jobportal.controllers;

import com.dso34bt.jobportal.model.CandidateAccount;
import com.dso34bt.jobportal.model.JobPost;
import com.dso34bt.jobportal.model.StaffAccount;
import com.dso34bt.jobportal.services.CandidateAccountService;
import com.dso34bt.jobportal.services.StaffAccountService;
import com.dso34bt.jobportal.utilities.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class AccountController {
    private final CandidateAccountService candidateAccountService;
    private final StaffAccountService staffAccountService;

    public AccountController(CandidateAccountService candidateAccountService, StaffAccountService staffAccountService) {
        this.candidateAccountService = candidateAccountService;
        this.staffAccountService = staffAccountService;
    }

    @GetMapping("login")
    public String candidateLogin(Model model) {
        model.addAttribute("user", new CandidateAccount());
        model.addAttribute("success", "");
        model.addAttribute("error", "");

        return "login";
    }

    @PostMapping("login")
    public String verifyCandidate(@ModelAttribute CandidateAccount user,
                                  Model model, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", new CandidateAccount());
            model.addAttribute("success", "");
            model.addAttribute("error", "");

            return "login";
        }

        if (!candidateAccountService.existsByEmail(user.getEmail())) {
            model.addAttribute("user", new CandidateAccount());
            model.addAttribute("success", "");
            model.addAttribute("error", "Invalid username or password!");

            return "login";
        }

        Optional<CandidateAccount> userDetails = candidateAccountService.getUserAccountByEmail(user.getEmail());

        userDetails.get().setLastLoginDate(Timestamp.valueOf(LocalDateTime.now()));

        if (!userDetails.get().getPassword().equals(user.getPassword())) {
            model.addAttribute("user", new CandidateAccount());
            model.addAttribute("success", "");
            model.addAttribute("error", "Invalid username or password!");

            return "login";
        }

        if (!candidateAccountService.saveCandidateAccount(userDetails.get())){
            model.addAttribute("success", "");
            model.addAttribute("error", "Failed to update user details");

            return "login";
        }

        // if details are verified and true, session user will be set and redirect to apply
        Session.setCandidateAccount(userDetails.get());
        return "redirect:/index";
    }

    @GetMapping("signup")
    public String candidateSignup(Model model) {
        model.addAttribute("user", new CandidateAccount());
        model.addAttribute("success", "");
        model.addAttribute("error", "");

        return "signup";
    }

    @GetMapping("change-password")
    public String candidateChangePassword(Model model) {
        model.addAttribute("user", new CandidateAccount());
        model.addAttribute("success", "");
        model.addAttribute("error", "");

        return "change-password";
    }

    @PostMapping("change-password")
    public String saveCandidateNewPassword(@ModelAttribute CandidateAccount user,
                                Model model, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", new CandidateAccount());
            model.addAttribute("success", "");
            model.addAttribute("error", "Invalid username or password");

            return "change-password";
        }

        if (!user.getPassword().equals(user.getConfirmPassword())) {
            model.addAttribute("user", new CandidateAccount());
            model.addAttribute("success", "");
            model.addAttribute("error", "Password does not match!");

            return "change-password";
        }

        if (candidateAccountService.existsByEmail(user.getEmail())){
            CandidateAccount account = candidateAccountService.getUserAccountByEmail(user.getEmail()).get();
            account.setPassword(user.getPassword());

            if (!candidateAccountService.saveCandidateAccount(account)){
                model.addAttribute("user", new CandidateAccount());
                model.addAttribute("success", "");
                model.addAttribute("error", "Failed to save, try again later!");

                return "change-password";
            }
        }
        else {
            model.addAttribute("user", new CandidateAccount());
            model.addAttribute("success", "");
            model.addAttribute("error", "User does not exists!");

            return "change-password";
        }

        model.addAttribute("user", new CandidateAccount());
        model.addAttribute("success", "Password was successfully changed!");
        model.addAttribute("error", "");

        return "login";
    }

    @PostMapping("signup")
    public String saveCandidate(@ModelAttribute CandidateAccount user,
                                  Model model, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", new CandidateAccount());
            model.addAttribute("success", "");
            model.addAttribute("error", "Invalid username or password!");

            return "signup";
        }

        if (candidateAccountService.existsByEmail(user.getEmail())) {
            model.addAttribute("user", new CandidateAccount());
            model.addAttribute("success", "");
            model.addAttribute("error", "Username alreafy taken!");

            return "signup";
        }

        if (!user.getPassword().equals(user.getConfirmPassword())) {
            model.addAttribute("user", new CandidateAccount());
            model.addAttribute("success", "");
            model.addAttribute("error", "Password does not match!");

            return "signup";
        }

        user.setId(candidateAccountService.getLastId() + 1);
        user.setEmailNotificationActive(false);
        user.setRegistrationDate(Timestamp.valueOf(LocalDateTime.now()));
        user.setLastLoginDate(Timestamp.valueOf(LocalDateTime.now()));

        if (!candidateAccountService.saveCandidateAccount(user)){
            model.addAttribute("user", new CandidateAccount());
            model.addAttribute("success", "");
            model.addAttribute("error", "Failed to save user, try again later!");

            return "signup";
        }

        model.addAttribute("user", new CandidateAccount());
        model.addAttribute("success", "You've successfully signed up!");
        model.addAttribute("error", "");

        return "login";
    }

    @GetMapping("login-staff")
    public String staffLogin(Model model) {
        model.addAttribute("user", new StaffAccount());
        model.addAttribute("success", "");
        model.addAttribute("error", "");

        return "login-staff";
    }

    @PostMapping("login-staff")
    public String verifyStaff(@ModelAttribute StaffAccount user, Model model, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", new StaffAccount());
            model.addAttribute("success", "");
            model.addAttribute("error", "Invalid username or password!");
            return "login-staff";
        }

        if (!staffAccountService.existsByEmail(user.getEmail())) {
            model.addAttribute("user", new StaffAccount());
            model.addAttribute("success", "");
            model.addAttribute("error", "Invalid username or password!");

            return "login-staff";
        }

        Optional<StaffAccount> userDetails = staffAccountService.getUserAccountByEmail(user.getEmail());

        userDetails.get().setLastLoginDate(Timestamp.valueOf(LocalDateTime.now()));

        if (!userDetails.get().getPassword().equals(user.getPassword())) {
            model.addAttribute("user", new StaffAccount());
            model.addAttribute("success", "");
            model.addAttribute("error", "Invalid username or password!");

            return "login-staff";
        }

        if (!staffAccountService.saveStaffAccount(userDetails.get())){
            model.addAttribute("success", "");
            model.addAttribute("error", "Failed to update!");
        }

        // if details are verified and true, session user will be set and redirect to dashboard
        Session.setStaffAccount(userDetails.get());
        model.addAttribute("jobPost", new JobPost());
        model.addAttribute("user", Session.getStaffAccount());
        return "job-post";

    }

    @GetMapping("forgot-password")
    public String staffChangePassword(Model model) {
        model.addAttribute("user", new StaffAccount());
        model.addAttribute("success", "");
        model.addAttribute("error", "");
        return "forgot-password";
    }

    @PostMapping("forgot-password")
    public String saveStaffNewPassword(@ModelAttribute StaffAccount user,
                                           Model model, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", new StaffAccount());
            model.addAttribute("success", "");
            model.addAttribute("error", "Invalid username or password!");

            return "forgot-password";
        }

        if (!user.getPassword().equals(user.getConfirmPassword())) {
            model.addAttribute("user", new StaffAccount());
            model.addAttribute("success", "");
            model.addAttribute("error", "Invalid username or password!");

            return "forgot-password";
        }

        if (staffAccountService.existsByEmail(user.getEmail())){
            StaffAccount account = staffAccountService.getUserAccountByEmail(user.getEmail()).get();
            account.setPassword(user.getPassword());

            if (!staffAccountService.saveStaffAccount(account)){
                model.addAttribute("user", new StaffAccount());
                model.addAttribute("success", "");
                model.addAttribute("error", "Failed to save, try again later!");

                return "forgot-password";
            }
        }

        model.addAttribute("user", new StaffAccount());
        model.addAttribute("success", "Successfully changed your password!");
        model.addAttribute("error", "");

        return "login-staff";
    }
}
