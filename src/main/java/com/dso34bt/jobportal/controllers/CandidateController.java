package com.dso34bt.jobportal.controllers;

import com.dso34bt.jobportal.model.*;
import com.dso34bt.jobportal.services.CandidateService;
import com.dso34bt.jobportal.services.DocumentService;
import com.dso34bt.jobportal.services.ExperienceService;
import com.dso34bt.jobportal.services.QualificationService;
import com.dso34bt.jobportal.utilities.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CandidateController {
    private final CandidateService candidateService;
    private final DocumentService documentService;

    public CandidateController(CandidateService candidateService, DocumentService documentService) {
        this.candidateService = candidateService;
        this.documentService = documentService;
    }

    @GetMapping("profile")
    public String profile(Model model, @RequestParam(value = "id", required = false) String id){
        if (Session.getCandidateAccount() == null) {
            model.addAttribute("user", new CandidateAccount());

            return "login";
        }
        Candidate candidate = new Candidate();

        CandidateAccount account = Session.getCandidateAccount();

        String success = "";
        String error = "";

        if (!documentService.existsByCandidateEmailAndTitle(account.getEmail(), "CV")){
            model.addAttribute("show", false);
            model.addAttribute("success", success);
            model.addAttribute("error", "ERROR: You must upload a 'CV' first. Go to 'Files' and upload it");
            model.addAttribute("candidate", candidate);
            model.addAttribute("user", Session.getCandidateAccount());

            return "profile";
        }

        if (id != null){
            try {
                if (candidateService.deleteCandidate(Long.parseLong(id)))
                    success = "Successfully deleted the candidate profile!";
            }
            catch (Exception e){
                error = "Failed to delete! Caused by: " + e.getMessage();
            }
        }

        if (candidateService.getCandidateByEmail(Session.getCandidateAccount().getEmail()).isPresent()) {
            candidate = candidateService.getCandidateByEmail(Session.getCandidateAccount().getEmail()).get();
        }
        else
            candidate.setCandidateAccount(Session.getCandidateAccount());


        model.addAttribute("show", true);
        model.addAttribute("success", success);
        model.addAttribute("error", error);
        model.addAttribute("candidate", candidate);
        model.addAttribute("user", Session.getCandidateAccount());

        return "profile";
    }

    @PostMapping("profile")
    public String storeProfile(@ModelAttribute Candidate candidate, Model model){
        if (Session.getCandidateAccount() == null) {
            model.addAttribute("user", new CandidateAccount());

            return "login";
        }
        String success = "";
        String error = "";

        candidate.setCandidateAccount(Session.getCandidateAccount());

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
        model.addAttribute("user", Session.getCandidateAccount());

        return "profile";
    }
}
