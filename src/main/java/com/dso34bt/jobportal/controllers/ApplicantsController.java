package com.dso34bt.jobportal.controllers;

import com.dso34bt.jobportal.model.*;
import com.dso34bt.jobportal.services.CandidateService;
import com.dso34bt.jobportal.services.JobPostActivityService;
import com.dso34bt.jobportal.services.JobPostService;
import com.dso34bt.jobportal.services.RecruiterService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ApplicantsController {
    private final CandidateService candidateService;
    private final RecruiterService recruiterService;
    private final JobPostService jobPostService;
    private final JobPostActivityService jobPostActivityService;

    public ApplicantsController(CandidateService candidateService,
                                RecruiterService recruiterService,
                                JobPostService jobPostService,
                                JobPostActivityService jobPostActivityService) {
        this.candidateService = candidateService;
        this.recruiterService = recruiterService;
        this.jobPostService = jobPostService;
        this.jobPostActivityService = jobPostActivityService;
    }

    @GetMapping("applicants")
    public String experience(Model model, @RequestParam(value = "search", required = false) String search,
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

        if (!user.getRole().equalsIgnoreCase("recruiter")) {
            model.addAttribute("user", new User());
            model.addAttribute("success", "");
            model.addAttribute("error", "You are not authorized to access this page!");

            return "login";
        }
        String success = "";
        String error = "";

        List<Applicant> applicants = new ArrayList<>();

        if (!jobPostActivityService.existsByRecruiterEmail(user.getEmail())) {
            model.addAttribute("user", user);
            model.addAttribute("success", "");
            model.addAttribute("error", "No one applied for any of the job posts you have posted");
            model.addAttribute("applicants", applicants);

            return "applicants";
        }
        Recruiter recruiter = recruiterService.getRecruiterByEmail(user.getEmail()).get();

        List<JobPostActivity> activityList = jobPostActivityService.findByJobPostRecruiterEmail(recruiter.getEmail());

        if (search != null){

            // search
            for (JobPostActivity activity : activityList) {
                if (activity.getJobPost().getTitle().equalsIgnoreCase(search)) {
                    // add results
                    applicants.add(new Applicant(activity.getCandidate(), activity.getJobPost(), activity));
                }
            }

            if (!applicants.isEmpty()){
                model.addAttribute("user", user);
                model.addAttribute("success", "Results found!");
                model.addAttribute("error", "");
                model.addAttribute("applicants", applicants);

                return "applicants";
            }
            else {
                error = "No results found";
            }
        }

        for (JobPostActivity activity : activityList)
            applicants.add(new Applicant(activity.getCandidate(), activity.getJobPost(), activity));

        model.addAttribute("user", user);
        model.addAttribute("success", success);
        model.addAttribute("error", error);
        model.addAttribute("applicants", applicants);

        return "applicants";
    }
}
