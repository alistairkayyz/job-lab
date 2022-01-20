package com.dso34bt.jobportal.controllers;

import com.dso34bt.jobportal.model.JobPost;
import com.dso34bt.jobportal.model.Staff;
import com.dso34bt.jobportal.model.StaffAccount;
import com.dso34bt.jobportal.model.Team;
import com.dso34bt.jobportal.services.StaffAccountService;
import com.dso34bt.jobportal.services.StaffService;
import com.dso34bt.jobportal.utilities.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Controller
public class StaffController {
    private final StaffService staffService;
    private final StaffAccountService accountService;

    public StaffController(StaffService staffService, StaffAccountService accountService) {
        this.staffService = staffService;
        this.accountService = accountService;
    }

    @GetMapping("team")
    public String jobPost(Model model, @RequestParam(value = "id", required = false) String id) {
        if (Session.getStaffAccount() == null) {
            model.addAttribute("user", new StaffAccount());
            model.addAttribute("success", "");
            model.addAttribute("error", "You must login first!");

            return "login-staff";
        }

        model.addAttribute("success", "");
        model.addAttribute("error", "");
        model.addAttribute("team", new Team());
        model.addAttribute("user", Session.getStaffAccount());
        return "team";
    }

    @PostMapping("team")
    public String saveTeam(@ModelAttribute Team team, Model model) {
        if (Session.getStaffAccount() == null) {
            model.addAttribute("user", new StaffAccount());
            model.addAttribute("success", "");
            model.addAttribute("error", "You must login first!");

            return "login-staff";
        }

        if (staffService.existsByEmail(team.getEmail())){
            model.addAttribute("success", "");
            model.addAttribute("error", "Staff with this email already exists.");
            model.addAttribute("team", team);
            model.addAttribute("user", Session.getStaffAccount());

            return "team";
        }

        if (!team.getPassword().equals(team.getConfirmPassword())){
            model.addAttribute("success", "");
            model.addAttribute("error", "Password does not match.");
            model.addAttribute("team", team);
            model.addAttribute("user", Session.getStaffAccount());

            return "team";
        }

        String success = "";
        String error = "";

        Staff staff = new Staff();
        StaffAccount staffAccount = new StaffAccount();

        staffAccount.setId(accountService.getLastId() + 1);
        staffAccount.setEmail(team.getEmail());
        staffAccount.setEmailNotificationActive(true);
        staffAccount.setPassword(team.getPassword());
        staffAccount.setLastLoginDate(Timestamp.valueOf(LocalDateTime.now()));
        staffAccount.setRegistrationDate(Timestamp.valueOf(LocalDateTime.now()));
        staffAccount.setStaffType(team.getRole());

        staff.setId(staffService.getLastId());
        staff.setDesignation(team.getRole());
        staff.setFirst_name(team.getName());
        staff.setLast_name(team.getLastname());
        staff.setStaffAccount(staffAccount);

        try {
            if (accountService.saveStaffAccount(staffAccount)) {
                if (staffService.saveStaff(staff))
                    success = "Successfully saved new staff!";
                else
                    error = "Failed to save staff!";
            }
            else
                error = "Failed to save account!";
        }
        catch (Exception exception){
            error = exception.getMessage();
        }

        model.addAttribute("success", success);
        model.addAttribute("error", error);
        model.addAttribute("team", team);
        model.addAttribute("user", Session.getStaffAccount());
        return "team";
    }
}
