package com.dso34bt.jobportal.controllers;

import com.dso34bt.jobportal.model.*;
import com.dso34bt.jobportal.services.*;
import com.dso34bt.jobportal.utilities.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class JobsController {
    private final CandidateAccountService candidateAccountService;
    private final CandidateService candidateService;
    private final DocumentService documentService;
    private final JobPostService jobPostService;
    private final JobPostActivityService activityService;
    private final StaffService staffService;

    public JobsController(CandidateAccountService candidateAccountService, CandidateService candidateService,
                          DocumentService documentService, JobPostService jobPostService,
                          JobPostActivityService activityService, StaffService staffService) {
        this.candidateAccountService = candidateAccountService;
        this.candidateService = candidateService;
        this.documentService = documentService;
        this.jobPostService = jobPostService;
        this.activityService = activityService;
        this.staffService = staffService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("user", Session.getCandidateAccount());

        if (!jobPostService.getJobPosts().isEmpty())
            model.addAttribute("jobPosts", jobPostService.getJobPosts());
        else
            model.addAttribute("jobPosts", new ArrayList<>());

        return "index";
    }

    @GetMapping("logout")
    public String logout(Model model) {

        if (Session.getCandidateAccount() != null){
            Session.setCandidateAccount(null);

            model.addAttribute("user", Session.getCandidateAccount());

            if (!jobPostService.getJobPosts().isEmpty())
                model.addAttribute("jobPosts", jobPostService.getJobPosts());
            else
                model.addAttribute("jobPosts", new ArrayList<>());

            return "index";
        }

        if (Session.getStaffAccount() != null){
            Session.setStaffAccount(null);
            model.addAttribute("user", new StaffAccount());
            return "login-staff";
        }

        model.addAttribute("user", null);

        if (!jobPostService.getJobPosts().isEmpty())
            model.addAttribute("jobPosts", jobPostService.getJobPosts());
        else
            model.addAttribute("jobPosts", new ArrayList<>());

        return "index";
    }

    @GetMapping("index")
    public String index(Model model) {
        model.addAttribute("user", Session.getCandidateAccount());

        if (!jobPostService.getJobPosts().isEmpty())
            model.addAttribute("jobPosts", jobPostService.getJobPosts());
        else
            model.addAttribute("jobPosts", new ArrayList<>());

        return "index";
    }

    @GetMapping("thanks")
    public String thanks(Model model) {
        if (Session.getCandidateAccount() == null) {
            model.addAttribute("user", new CandidateAccount());
            model.addAttribute("success", "");
            model.addAttribute("error", "You must login first!");

            return "login";
        }

        model.addAttribute("user", Session.getCandidateAccount());
        return "thanks";
    }

    @GetMapping("job-post")
    public String jobPost(Model model, @RequestParam(value = "id", required = false) String id) {
        if (Session.getStaffAccount() == null) {
            model.addAttribute("user", new StaffAccount());
            model.addAttribute("success", "");
            model.addAttribute("error", "You must login first!");

            return "login-staff";
        }

        if (id != null)
            model.addAttribute("jobPost", jobPostService.getJobPostByID(Long.parseLong(id)));
        else {
            model.addAttribute("jobPost", new JobPost());
        }

        model.addAttribute("user", Session.getStaffAccount());
        return "job-post";
    }

    @PostMapping("job-post")
    public String saveJob(@ModelAttribute JobPost jobPost, Model model) {
        if (Session.getStaffAccount() == null) {
            model.addAttribute("user", new StaffAccount());
            model.addAttribute("success", "");
            model.addAttribute("error", "You must login first!");

            return "login-staff";
        }

        if (jobPost.getId() != null && jobPostService.getJobPostByID(jobPost.getId()).isPresent()) {
            System.out.println("*********Present");
            LocalDateTime localDateTime = LocalDateTime.parse(jobPost.getTimestamp());
            jobPost.setClosingDate(Timestamp.valueOf(localDateTime));
            jobPost.setCreatedDate(Date.valueOf(LocalDate.now()));
            jobPostService.saveJobPost(jobPost);
        }
        else {

            LocalDateTime localDateTime = LocalDateTime.parse(jobPost.getTimestamp());
            jobPost.setId(jobPostService.getLastId() + 1);
            jobPost.setClosingDate(Timestamp.valueOf(localDateTime));
            jobPost.setCreatedDate(Date.valueOf(LocalDate.now()));
            jobPost.setStaff(staffService.getStaffByEmail(Session.getStaffAccount().getEmail()).get());

            jobPostService.saveJobPost(jobPost);
        }
        model.addAttribute("jobPost", jobPost);

        model.addAttribute("user", Session.getStaffAccount());
        return "job-post";
    }

    @GetMapping("view-jobs")
    public String viewJobs(Model model, @RequestParam(value = "id", required = false) String id) {
        if (Session.getStaffAccount() == null) {
            model.addAttribute("user", new StaffAccount());
            model.addAttribute("success", "");
            model.addAttribute("error", "You must login first!");

            return "login-staff";
        }
        if (id != null && jobPostService.deleteJobPostById(Long.parseLong(id)))
            System.out.println("deleted");

        model.addAttribute("jobs", jobPostService.getJobPosts());

        model.addAttribute("user", Session.getStaffAccount());
        return "view-jobs";
    }

    @GetMapping("job")
    public String apply(Model model, @RequestParam(value = "id") String id){
        if (Session.getCandidateAccount() == null) {
            model.addAttribute("user", new CandidateAccount());
            model.addAttribute("success", "");
            model.addAttribute("error", "You must login first!");

            return "login";
        }

        CandidateAccount account = Session.getCandidateAccount();

        if (!documentService.existsByCandidateEmailAndTitle(account.getEmail(), "CV")){
            model.addAttribute("show", false);
            model.addAttribute("user", Session.getCandidateAccount());

            return "apply";
        }

        JobPost jobPost = jobPostService.getJobPostByID(Long.parseLong(id)).orElse(new JobPost());

        Candidate candidate = candidateService.getCandidateByEmail(Session.getCandidateAccount().getEmail()).get();

        boolean status = activityService.applied(jobPost.getId(), candidate.getId());

        model.addAttribute("show", true);
        model.addAttribute("status", status);
        model.addAttribute("jobPost", jobPost);
        model.addAttribute("user", Session.getCandidateAccount());

        return "apply";
    }

    @GetMapping("apply")
    public String applying(Model model, @RequestParam(value = "id") String id){
        if (Session.getCandidateAccount() == null) {
            model.addAttribute("user", new CandidateAccount());
            model.addAttribute("success", "");
            model.addAttribute("error", "You must login first!");

            return "login";
        }

        if (id != null) {
            JobPostActivity jobPostActivity = new JobPostActivity();
            jobPostActivity.setJobPost(jobPostService.getJobPostByID(Long.parseLong(id)).get());
            jobPostActivity.setCandidate(candidateService.getCandidateByEmail(Session.getCandidateAccount().getEmail()).get());
            jobPostActivity.setDate(Date.valueOf(LocalDate.now()));
            jobPostActivity.setStatus("IN PROGRESS");

            if (activityService.saveJobPostActivity(jobPostActivity)){
                System.out.println("Applied for: " + jobPostService.getJobPostByID(Long.parseLong(id)).get().getTitle());

                model.addAttribute("user", Session.getCandidateAccount());

                return "thanks";
            }

            model.addAttribute("user", Session.getCandidateAccount());

            JobPost jobPost = jobPostService.getJobPostByID(Long.parseLong(id)).get();
            model.addAttribute("jobPost", jobPost);

            return "apply";
        }

        model.addAttribute("user", Session.getCandidateAccount());
        return "index";
    }
}