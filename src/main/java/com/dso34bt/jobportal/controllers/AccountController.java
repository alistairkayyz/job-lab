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
        return "login";
    }

    @PostMapping("login")
    public String verifyCandidate(@ModelAttribute CandidateAccount user,
                                  Model model, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", new CandidateAccount());
            return "login";
        }

        if (!candidateAccountService.existsByEmail(user.getEmail())) {
            model.addAttribute("user", new CandidateAccount());
            return "login";
        }

        Optional<CandidateAccount> userDetails = candidateAccountService.getUserAccountByEmail(user.getEmail());

        userDetails.get().setLastLoginDate(Timestamp.valueOf(LocalDateTime.now()));

        if (!userDetails.get().getPassword().equals(user.getPassword())) {
            model.addAttribute("user", new CandidateAccount());
            return "login";
        }

        if (!candidateAccountService.saveCandidateAccount(userDetails.get()))
            System.out.println("Failed to update");

        // if details are verified and true, session user will be set and redirect to apply
        Session.setCandidateAccount(userDetails.get());
        return "redirect:/index";
    }

    @GetMapping("signup")
    public String candidateSignup(Model model) {
        model.addAttribute("user", new CandidateAccount());
        return "signup";
    }

    @GetMapping("change-password")
    public String candidateChangePassword(Model model) {
        model.addAttribute("user", new CandidateAccount());
        return "change-password";
    }

    @PostMapping("change-password")
    public String saveCandidateNewPassword(@ModelAttribute CandidateAccount user,
                                Model model, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", new CandidateAccount());
            return "change-password";
        }

        if (!user.getPassword().equals(user.getConfirmPassword())) {
            model.addAttribute("user", new CandidateAccount());
            return "change-password";
        }

        if (candidateAccountService.existsByEmail(user.getEmail())){
            CandidateAccount account = candidateAccountService.getUserAccountByEmail(user.getEmail()).get();
            account.setPassword(user.getPassword());

            if (!candidateAccountService.saveCandidateAccount(account)){
                model.addAttribute("user", new CandidateAccount());
                return "change-password";
            }
        }

        model.addAttribute("user", new CandidateAccount());

        return "login";
    }

    @PostMapping("signup")
    public String saveCandidate(@ModelAttribute CandidateAccount user,
                                  Model model, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", new CandidateAccount());
            return "signup";
        }

        if (!user.getPassword().equals(user.getConfirmPassword())) {
            model.addAttribute("user", new CandidateAccount());
            return "signup";
        }
        CandidateAccount account = user;

        account.setId(candidateAccountService.getLastId() + 1);
        account.setEmailNotificationActive(false);
        account.setRegistrationDate(Timestamp.valueOf(LocalDateTime.now()));
        account.setLastLoginDate(Timestamp.valueOf(LocalDateTime.now()));

        if (!candidateAccountService.saveCandidateAccount(account)){
            model.addAttribute("user", new CandidateAccount());
            return "signup";
        }

        model.addAttribute("user", new CandidateAccount());

        return "login";
    }

    @GetMapping("login-staff")
    public String staffLogin(Model model) {
        model.addAttribute("user", new StaffAccount());
        return "login-staff";
    }

    @PostMapping("login-staff")
    public String verifyStaff(@ModelAttribute StaffAccount user, Model model, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", new StaffAccount());
            return "login-staff";
        }

        if (!staffAccountService.existsByEmail(user.getEmail())) {
            model.addAttribute("user", new StaffAccount());

            return "login-staff";
        }

        Optional<StaffAccount> userDetails = staffAccountService.getUserAccountByEmail(user.getEmail());

        userDetails.get().setLastLoginDate(Timestamp.valueOf(LocalDateTime.now()));

        if (!userDetails.get().getPassword().equals(user.getPassword())) {
            model.addAttribute("user", new StaffAccount());

            return "login-staff";
        }

        if (!staffAccountService.saveStaffAccount(userDetails.get()))
            System.out.println("Failed to update");

        // if details are verified and true, session user will be set and redirect to dashboard
        Session.setStaffAccount(userDetails.get());
        model.addAttribute("jobPost", new JobPost());
        model.addAttribute("user", Session.getStaffAccount());
        return "job-post";

    }

    @GetMapping("forgot-password")
    public String staffChangePassword(Model model) {
        model.addAttribute("user", new StaffAccount());
        return "forgot-password";
    }

    @PostMapping("forgot-password")
    public String saveStaffNewPassword(@ModelAttribute StaffAccount user,
                                           Model model, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", new StaffAccount());
            return "forgot-password";
        }

        if (!user.getPassword().equals(user.getConfirmPassword())) {
            model.addAttribute("user", new StaffAccount());
            return "forgot-password";
        }

        if (staffAccountService.existsByEmail(user.getEmail())){
            StaffAccount account = staffAccountService.getUserAccountByEmail(user.getEmail()).get();
            account.setPassword(user.getPassword());

            if (!staffAccountService.saveStaffAccount(account)){
                model.addAttribute("user", new StaffAccount());
                return "forgot-password";
            }
        }

        model.addAttribute("user", new StaffAccount());

        return "login-staff";
    }
}
