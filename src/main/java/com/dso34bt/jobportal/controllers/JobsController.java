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

@Controller
public class JobsController {
    private final CandidateAccountService candidateAccountService;
    private final CandidateService candidateService;
    private final DocumentService documentService;
    private final JobPostService jobPostService;
    private final JobPostActivityService activityService;
    private final RecruiterService recruiterService;

    public JobsController(CandidateAccountService candidateAccountService, CandidateService candidateService,
                          DocumentService documentService, JobPostService jobPostService,
                          JobPostActivityService activityService, RecruiterService recruiterService) {
        this.candidateAccountService = candidateAccountService;
        this.candidateService = candidateService;
        this.documentService = documentService;
        this.jobPostService = jobPostService;
        this.activityService = activityService;
        this.recruiterService = recruiterService;
    }

    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute("user", Session.getUser());

        if (!jobPostService.getJobPosts().isEmpty())
            model.addAttribute("jobPosts", jobPostService.getJobPosts());
        else
            model.addAttribute("jobPosts", new ArrayList<>());

        return "index";
    }

    @GetMapping("logout")
    public String logout(Model model) {

        if (Session.getUser() != null && Session.getUser().getRole().equalsIgnoreCase("candidate")) {
            Session.setUser(null);

            model.addAttribute("user", Session.getUser());

            if (!jobPostService.getJobPosts().isEmpty())
                model.addAttribute("jobPosts", jobPostService.getJobPosts());
            else
                model.addAttribute("jobPosts", new ArrayList<>());

            return "index";
        }

        if (Session.getUser() != null && !Session.getUser().getRole().equalsIgnoreCase("candidate")) {
            Session.setUser(null);
            model.addAttribute("user", new User());
            model.addAttribute("success", "You have successfully signed out");
            model.addAttribute("error", "");

            return "login";
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
        model.addAttribute("user", Session.getUser());

        if (!jobPostService.getJobPosts().isEmpty())
            model.addAttribute("jobPosts", jobPostService.getJobPosts());
        else
            model.addAttribute("jobPosts", new ArrayList<>());

        return "index";
    }

    @GetMapping("thanks")
    public String thanks(Model model) {
        if (Session.getUser() == null) {
            model.addAttribute("user", new User());
            model.addAttribute("success", "");
            model.addAttribute("error", "You must login first!");

            return "login";
        }

        if (Session.getUser().getRole().equalsIgnoreCase("recruiter")) {
            Session.setUser(null);
            model.addAttribute("user", new User());
            model.addAttribute("success", "");
            model.addAttribute("error", "You are not authorized to access this page!");

            return "login";
        }

        model.addAttribute("user", Session.getUser());
        return "thanks";
    }

    @GetMapping("job-post")
    public String jobPost(Model model, @RequestParam(value = "id", required = false) String id) {
        if (Session.getUser() == null) {
            model.addAttribute("user", new User());
            model.addAttribute("success", "");
            model.addAttribute("error", "You must login first!");

            return "login";
        }

        if (!Session.getUser().getRole().equalsIgnoreCase("recruiter")) {
            Session.setUser(null);
            model.addAttribute("user", new User());
            model.addAttribute("success", "");
            model.addAttribute("error", "You are not authorized to access this page!");

            return "login";
        }

        String success = "";
        String error = "";

        if (id != null)
            model.addAttribute("jobPost", jobPostService.getJobPostByID(Long.parseLong(id)));
        else {
            model.addAttribute("jobPost", new JobPost());
        }

        model.addAttribute("error", error);
        model.addAttribute("success", success);
        model.addAttribute("user", Session.getUser());
        return "job-post";
    }

    @PostMapping("job-post")
    public String saveJob(@ModelAttribute JobPost jobPost, Model model) {
        if (Session.getUser() == null) {
            model.addAttribute("user", new Recruiter());
            model.addAttribute("success", "");
            model.addAttribute("error", "You must login first!");

            return "recruiter-signup";
        }

        String success = "";
        String error = "";

        if (jobPost.getId() != null && jobPostService.getJobPostByID(jobPost.getId()).isPresent()) {
            System.out.println("*********Present");
            LocalDateTime localDateTime = LocalDateTime.parse(jobPost.getTimestamp());
            jobPost.setClosingDate(Timestamp.valueOf(localDateTime));
            jobPost.setCreatedDate(Date.valueOf(LocalDate.now()));
            jobPost.setRecruiter(recruiterService.getRecruiterByEmail(Session.getUser().getEmail()).get());

            try {
                if (jobPostService.saveJobPost(jobPost))
                    success = "Successfully updated this Job Post";
                else
                    error = "Failed to update this job post";
            } catch (Exception exception) {
                error = exception.getMessage();
            }
        } else {
            LocalDateTime localDateTime = LocalDateTime.parse(jobPost.getTimestamp());
            jobPost.setId(jobPostService.getLastId() + 1);
            jobPost.setClosingDate(Timestamp.valueOf(localDateTime));
            jobPost.setCreatedDate(Date.valueOf(LocalDate.now()));
            jobPost.setRecruiter(recruiterService.getRecruiterByEmail(Session.getUser().getEmail()).get());

            try {
                if (jobPostService.saveJobPost(jobPost))
                    success = "Successfully saved this Job Post";
                else
                    error = "Failed to save this job post";
            } catch (Exception exception) {
                error = exception.getMessage();
            }
        }

        model.addAttribute("error", error);
        model.addAttribute("success", success);
        model.addAttribute("jobPost", jobPost);
        model.addAttribute("user", Session.getUser());

        return "job-post";
    }

    @GetMapping("view-jobs")
    public String viewJobs(Model model, @RequestParam(value = "id", required = false) String id) {
        if (Session.getUser() == null) {
            model.addAttribute("user", new User());
            model.addAttribute("success", "");
            model.addAttribute("error", "You must login first!");

            return "login";
        }

        if (!Session.getUser().getRole().equalsIgnoreCase("recruiter")) {
            Session.setUser(null);
            model.addAttribute("user", new User());
            model.addAttribute("success", "");
            model.addAttribute("error", "You are not authorized to access this page!");

            return "login";
        }
        String success = "";
        String error = "";

        if (id != null) {
            JobPost jobPost = jobPostService.getJobPostByID(Long.parseLong(id)).get();

            if (jobPostService.deleteJobPostById(Long.parseLong(id)))
                success = "Successfully deleted " + jobPost.getTitle();
            else
                error = "Failed to delete " + jobPost.getTitle();
        }

        Recruiter recruiter = recruiterService.getRecruiterByEmail(Session.getUser().getEmail()).get();

        model.addAttribute("error", error);
        model.addAttribute("success", success);
        model.addAttribute("jobs", jobPostService.getByRecruiterId(recruiter.getId()));
        model.addAttribute("user", Session.getUser());

        return "view-jobs";
    }

    @GetMapping("job")
    public String apply(Model model, @RequestParam(value = "id") String id) {
        if (Session.getUser() == null) {
            model.addAttribute("user", new User());
            model.addAttribute("success", "");
            model.addAttribute("error", "You must login first!");

            return "login";
        }

        if (Session.getUser().getRole().equalsIgnoreCase("recruiter")) {
            Session.setUser(null);
            model.addAttribute("user", new User());
            model.addAttribute("success", "");
            model.addAttribute("error", "You are not authorized to access this page!");

            return "login";
        }

        CandidateAccount account = candidateAccountService.getUserAccountByEmail(Session.getUser().getEmail()).get();

        if (!documentService.existsByCandidateEmailAndTitle(account.getEmail(), "CV")) {
            model.addAttribute("show", false);
            model.addAttribute("user", Session.getUser());

            return "apply";
        }

        JobPost jobPost = jobPostService.getJobPostByID(Long.parseLong(id)).orElse(new JobPost());

        Candidate candidate = candidateService.getCandidateByEmail(Session.getUser().getEmail()).get();

        boolean status = activityService.applied(jobPost.getId(), candidate.getId());

        model.addAttribute("show", true);
        model.addAttribute("status", status);
        model.addAttribute("jobPost", jobPost);
        model.addAttribute("user", Session.getUser());

        return "apply";
    }

    @GetMapping("apply")
    public String applying(Model model, @RequestParam(value = "id") String id) {
        if (Session.getUser() == null) {
            model.addAttribute("user", new User());
            model.addAttribute("success", "");
            model.addAttribute("error", "You must login first!");

            return "login";
        }

        if (Session.getUser().getRole().equalsIgnoreCase("recruiter")) {
            Session.setUser(null);
            model.addAttribute("user", new User());
            model.addAttribute("success", "");
            model.addAttribute("error", "You are not authorized to access this page!");

            return "login";
        }

        if (id != null) {
            JobPostActivity jobPostActivity = new JobPostActivity();
            jobPostActivity.setJobPost(jobPostService.getJobPostByID(Long.parseLong(id)).get());
            jobPostActivity.setCandidate(candidateService.getCandidateByEmail(Session.getUser().getEmail()).get());
            jobPostActivity.setDate(Date.valueOf(LocalDate.now()));
            jobPostActivity.setStatus("IN PROGRESS");

            if (activityService.saveJobPostActivity(jobPostActivity)) {
                System.out.println("Applied for: " + jobPostService.getJobPostByID(Long.parseLong(id)).get().getTitle());

                model.addAttribute("user", Session.getUser());

                return "thanks";
            }

            model.addAttribute("user", Session.getUser());

            JobPost jobPost = jobPostService.getJobPostByID(Long.parseLong(id)).get();
            model.addAttribute("jobPost", jobPost);

            return "apply";
        }

        model.addAttribute("user", Session.getUser());
        return "index";
    }
}