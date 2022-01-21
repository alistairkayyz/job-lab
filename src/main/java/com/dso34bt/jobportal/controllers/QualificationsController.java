package com.dso34bt.jobportal.controllers;

import com.dso34bt.jobportal.model.*;
import com.dso34bt.jobportal.services.CandidateAccountService;
import com.dso34bt.jobportal.services.CandidateService;
import com.dso34bt.jobportal.services.DocumentService;
import com.dso34bt.jobportal.services.QualificationService;
import com.dso34bt.jobportal.utilities.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class QualificationsController {
    private final QualificationService qualificationService;
    private final DocumentService documentService;
    private final CandidateAccountService candidateAccountService;

    public QualificationsController(QualificationService qualificationService,
                                    DocumentService documentService,
                                    CandidateAccountService candidateAccountService) {
        this.qualificationService = qualificationService;
        this.documentService = documentService;
        this.candidateAccountService = candidateAccountService;
    }

    @GetMapping("qualifications")
    public String qualifications(Model model, @RequestParam(value = "id", required = false) String id,
                                 @RequestParam(value = "action", required = false) String action){
        if (Session.getUser() == null) {
            model.addAttribute("user", new User());
            model.addAttribute("success", "");
            model.addAttribute("error", "You must login first!");

            return "login";
        }

        if (Session.getUser().getRole().equalsIgnoreCase("recruiter")){
            Session.setUser(null);
            model.addAttribute("user", new User());
            model.addAttribute("success", "");
            model.addAttribute("error", "You are not authorized to access this page!");

            return "login";
        }

        List<Qualifications> qualificationsList = new ArrayList<>();

        CandidateAccount account = candidateAccountService.getUserAccountByEmail(Session.getUser().getEmail()).get();

        String success = "";
        String error = "";

        // get stored files if they exist
        if (qualificationService.existsByEmail(account.getEmail()))
            qualificationsList = qualificationService.findByCandidateEmail(account.getEmail());

        if (id != null && action != null){
            if (action.equalsIgnoreCase("delete") &&
                    qualificationService.deleteQualification(Long.parseLong(id))){
                success = "Qualification successfully deleted!";
            }

            if (action.equalsIgnoreCase("edit")){
                model.addAttribute("show", true);
                model.addAttribute("success", success);
                model.addAttribute("error", error);
                model.addAttribute("qualification", qualificationService.findByID(Long.parseLong(id)));
                model.addAttribute("qualificationsList", qualificationsList);
                model.addAttribute("user", Session.getUser());

                return "qualifications";
            }
        }

        model.addAttribute("show", true);
        model.addAttribute("success", success);
        model.addAttribute("error", error);
        model.addAttribute("qualification", new Qualifications());
        model.addAttribute("qualificationsList", qualificationService.findByCandidateEmail(account.getEmail()));
        model.addAttribute("user", Session.getUser());

        return "qualifications";
    }

    @PostMapping("qualifications")
    public String storeQualifications(@ModelAttribute Qualifications qualifications, Model model){
        if (Session.getUser() == null) {
            model.addAttribute("user", new User());
            model.addAttribute("success", "");
            model.addAttribute("error", "You must login first!");

            return "login";
        }

        List<Qualifications> qualificationsList = new ArrayList<>();

        CandidateAccount account = candidateAccountService.getUserAccountByEmail(Session.getUser().getEmail()).get();

        String success = "";
        String error = "";

        qualifications.setCandidateAccount(account);

        // save the new Qualifications and update if exists
        if (qualifications.getId() == null || qualifications.getId() < 1)
            qualifications.setId(qualificationService.getLastId() + 1);

        if (qualificationService.saveQualification(qualifications)){
            System.out.println("Qualifications saved!");
            qualificationsList = qualificationService.findByCandidateEmail(account.getEmail());
        }
        else
            error = "Failed to save Qualification!";

        model.addAttribute("show", true);
        model.addAttribute("success", success);
        model.addAttribute("error", error);
        model.addAttribute("qualification", new Qualifications());
        model.addAttribute("qualificationsList", qualificationsList);
        model.addAttribute("user", Session.getUser());

        return "qualifications";
    }
}
