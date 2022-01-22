package com.dso34bt.jobportal.controllers;

import com.dso34bt.jobportal.model.*;
import com.dso34bt.jobportal.services.CandidateAccountService;
import com.dso34bt.jobportal.services.DocumentService;
import com.dso34bt.jobportal.services.ExperienceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ExperienceController {
    private final ExperienceService experienceService;
    private final DocumentService documentService;
    private final CandidateAccountService candidateAccountService;

    public ExperienceController(ExperienceService experienceService, DocumentService documentService,
                                CandidateAccountService candidateAccountService) {
        this.experienceService = experienceService;
        this.documentService = documentService;
        this.candidateAccountService = candidateAccountService;
    }

    @GetMapping("experience")
    public String experience(Model model, @RequestParam(value = "id", required = false) String id,
                             @RequestParam(value = "action", required = false) String action, HttpSession session) {
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
        
        if (user.getRole().equalsIgnoreCase("recruiter")){
            model.addAttribute("user", new User());
            model.addAttribute("success", "");
            model.addAttribute("error", "You are not authorized to access this page!");

            return "login";
        }

        List<Experience> experienceList = new ArrayList<>();

        CandidateAccount account = candidateAccountService.getUserAccountByEmail(user.getEmail()).get();

        String success = "";
        String error = "";

        // get stored files if they exist
        if (experienceService.existsByCandidateEmail(account.getEmail()))
            experienceList = experienceService.findByCandidateEmail(account.getEmail());

        // delete the existing document
        if (id != null && action != null){
            if (action.equalsIgnoreCase("delete") &&
                    experienceService.deleteExperience(Long.parseLong(id))){
                success = "Experience successfully deleted";
            }

            if (action.equalsIgnoreCase("edit")){
                model.addAttribute("show", true);
                model.addAttribute("success", success);
                model.addAttribute("error", error);
                model.addAttribute("experience", experienceService.findById(Long.parseLong(id)));
                model.addAttribute("experienceList", experienceList);
                model.addAttribute("user", user);

                return "experience";
            }
        }

        model.addAttribute("show", true);
        model.addAttribute("success", success);
        model.addAttribute("error", error);
        model.addAttribute("experience", new Experience());
        model.addAttribute("experienceList", experienceService.findByCandidateEmail(account.getEmail()));
        model.addAttribute("user", user);

        return "experience";
    }

    @PostMapping("experience")
    public String storeExperience(@ModelAttribute Experience experience, Model model, HttpSession session) {
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

        CandidateAccount account = candidateAccountService.getUserAccountByEmail(user.getEmail()).get();

        String success = "";
        String error = "";

        experience.setCandidateAccount(account);

        // save the new experience and update if exists
        if (experience.getId() == null || experience.getId() < 1)
            experience.setId(experienceService.getLastId() + 1);

        System.out.println(experienceService.getLastId());
        System.out.println(experience.getId());

        if (experienceService.saveExperience(experience)){
            success = "Experience saved!";
        }
        else
            error = "Failed to save!";

        List<Experience> experienceList = experienceService.findByCandidateEmail(account.getEmail());

        model.addAttribute("show", true);
        model.addAttribute("success", success);
        model.addAttribute("error", error);
        model.addAttribute("experience", new Experience());
        model.addAttribute("experienceList", experienceList);
        model.addAttribute("user", user);

        return "experience";
    }
}
