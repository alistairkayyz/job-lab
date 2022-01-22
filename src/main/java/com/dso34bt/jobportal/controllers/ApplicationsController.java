package com.dso34bt.jobportal.controllers;

import com.dso34bt.jobportal.model.Candidate;
import com.dso34bt.jobportal.model.JobPostActivity;
import com.dso34bt.jobportal.model.User;
import com.dso34bt.jobportal.services.CandidateService;
import com.dso34bt.jobportal.services.JobPostActivityService;
import com.dso34bt.jobportal.services.JobPostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
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
    public String getApplications(Model model, @RequestParam(value = "id", required = false) String id, HttpSession session) {
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

        Candidate candidate;

        if (candidateService.candidateExists(user.getEmail()))
            candidate = candidateService.getCandidateByEmail(user.getEmail()).get();
        else {
            model.addAttribute("candidate", new Candidate());
            model.addAttribute("user", user);

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

        model.addAttribute("user", user);
        model.addAttribute("applications", applications);

        return "applications";
    }
}
