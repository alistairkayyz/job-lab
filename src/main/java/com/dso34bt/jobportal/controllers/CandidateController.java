package com.dso34bt.jobportal.controllers;

import com.dso34bt.jobportal.model.*;
import com.dso34bt.jobportal.services.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class CandidateController {
    private final CandidateService candidateService;
    private final DocumentService documentService;
    private final CandidateAccountService candidateAccountService;

    public CandidateController(CandidateService candidateService, DocumentService documentService,
                               CandidateAccountService candidateAccountService) {
        this.candidateService = candidateService;
        this.documentService = documentService;
        this.candidateAccountService = candidateAccountService;
    }

    @GetMapping("profile")
    public String profile(Model model, @RequestParam(value = "id", required = false) String id, 
                          HttpSession session) {
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
        Candidate candidate = new Candidate();

        CandidateAccount account = candidateAccountService.getUserAccountByEmail(user.getEmail()).get();

        String success = "";
        String error = "";

        if (id != null){
            try {
                if (candidateService.deleteCandidate(Long.parseLong(id)))
                    success = "Successfully deleted the candidate profile!";
            }
            catch (Exception e){
                error = "Failed to delete! Caused by: " + e.getMessage();
            }
        }

        if (candidateService.getCandidateByEmail(user.getEmail()).isPresent()) {
            candidate = candidateService.getCandidateByEmail(user.getEmail()).get();
        }
        else
            candidate.setCandidateAccount(account);


        model.addAttribute("show", true);
        model.addAttribute("success", success);
        model.addAttribute("error", error);
        model.addAttribute("candidate", candidate);
        model.addAttribute("user", user);

        return "profile";
    }

    @PostMapping("profile")
    public String storeProfile(@ModelAttribute Candidate candidate, Model model, HttpSession session) {
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
        String success = "";
        String error = "";

        candidate.setCandidateAccount(candidateAccountService.getUserAccountByEmail(user.getEmail()).get());

        candidate.setId(candidateService.getLastId() + 1);

        if (candidateService.candidateExists(candidate.getCandidateAccount().getEmail())){
            candidate.setId(candidateService.getCandidateByEmail(candidate.getCandidateAccount().getEmail()).get().getId());
        }


        try{
            if (candidateService.saveCandidate(candidate)) {
                success = "Candidate saved!";

                candidate = candidateService.getCandidateByEmail(candidate.getCandidateAccount().getEmail()).get();
            }
        }
        catch (Exception e){
            error = "Failed to save! Caused by: " + e.getMessage();
        }

        model.addAttribute("show", true);
        model.addAttribute("success", success);
        model.addAttribute("error", error);
        model.addAttribute("candidate", candidate);
        model.addAttribute("user", user);

        return "profile";
    }
}
