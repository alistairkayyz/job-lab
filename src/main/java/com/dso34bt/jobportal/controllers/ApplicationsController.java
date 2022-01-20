package com.dso34bt.jobportal.controllers;

import com.dso34bt.jobportal.model.Candidate;
import com.dso34bt.jobportal.model.CandidateAccount;
import com.dso34bt.jobportal.model.JobPostActivity;
import com.dso34bt.jobportal.services.CandidateService;
import com.dso34bt.jobportal.services.JobPostActivityService;
import com.dso34bt.jobportal.services.JobPostService;
import com.dso34bt.jobportal.utilities.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ApplicationsController {

    private final JobPostService jobPostService;
    private final JobPostActivityService activityService;
    private final CandidateService candidateService;

    public ApplicationsController(JobPostService jobPostService,
                                  JobPostActivityService activityService,
                                  CandidateService candidateService) {
        this.jobPostService = jobPostService;
        this.activityService = activityService;
        this.candidateService = candidateService;
    }

    @GetMapping("applications")
    public String getApplications(Model model, @RequestParam(value = "id", required = false) String id){
        if (Session.getCandidateAccount() == null) {
            model.addAttribute("user", new CandidateAccount());
            model.addAttribute("success", "");
            model.addAttribute("error", "You must login first!");

            return "login";
        }
        Candidate candidate;

        if (candidateService.candidateExists(Session.getCandidateAccount().getEmail()))
            candidate = candidateService.getCandidateByEmail(Session.getCandidateAccount().getEmail()).get();
        else {
            model.addAttribute("candidate", new Candidate());
            model.addAttribute("user", Session.getCandidateAccount());

            return "profile";
        }

        if (id != null){
            JobPostActivity jobPostActivity = activityService.getById(Long.parseLong(id)).get();
            jobPostActivity.setStatus("WITHDRAWN");

            if (activityService.saveJobPostActivity(jobPostActivity))
                System.out.println("Status updated");
        }

        List<JobPostActivity> applications = new ArrayList<>();

        if (activityService.existsByCandidateId(candidate.getId()))
            applications = activityService.getByCandidateId(candidate.getId());

        model.addAttribute("user", Session.getCandidateAccount());
        model.addAttribute("applications", applications);

        return "applications";
    }
}
