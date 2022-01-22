package com.dso34bt.jobportal.controllers;

import com.dso34bt.jobportal.model.*;
import com.dso34bt.jobportal.services.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
    public String home(Model model, HttpSession session) {
        @SuppressWarnings("unchecked")
        List<User> userSessions = (List<User>) session.getAttribute("SESSIONS");

        if (userSessions == null) {
            model.addAttribute("user", null);
        }
        else
            model.addAttribute("user", userSessions.get(userSessions.size() - 1));

        if (!jobPostService.getJobPosts().isEmpty())
            model.addAttribute("jobPosts", jobPostService.getJobPosts());
        else
            model.addAttribute("jobPosts", new ArrayList<>());

        return "index";
    }

    @GetMapping("logout")
    public String logout(Model model, HttpServletRequest request, HttpSession session) {
        // destroy the session
        request.getSession().invalidate();

        model.addAttribute("user", new User());

        if (!jobPostService.getJobPosts().isEmpty())
            model.addAttribute("jobPosts", jobPostService.getJobPosts());
        else
            model.addAttribute("jobPosts", new ArrayList<>());

        return "index";
    }

    @GetMapping("index")
    public String index(Model model, HttpSession session) {
        @SuppressWarnings("unchecked")
        List<User> userSessions = (List<User>) session.getAttribute("SESSIONS");

        if (userSessions == null) {
            model.addAttribute("user", null);
        }
        else
            model.addAttribute("user", userSessions.get(userSessions.size() - 1));

        if (!jobPostService.getJobPosts().isEmpty())
            model.addAttribute("jobPosts", jobPostService.getJobPosts());
        else
            model.addAttribute("jobPosts", new ArrayList<>());

        return "index";
    }

    @GetMapping("thanks")
    public String thanks(Model model, HttpSession session) {
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

        if (user.getRole().equalsIgnoreCase("recruiter")) {
            model.addAttribute("user", new User());
            model.addAttribute("success", "");
            model.addAttribute("error", "You are not authorized to access this page!");

            return "login";
        }

        model.addAttribute("user", user);
        return "thanks";
    }

    @GetMapping("job-post")
    public String jobPost(Model model, @RequestParam(value = "id", required = false) String id,
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

        if (id != null)
            model.addAttribute("jobPost", jobPostService.getJobPostByID(Long.parseLong(id)));
        else {
            model.addAttribute("jobPost", new JobPost());
        }

        model.addAttribute("error", error);
        model.addAttribute("success", success);
        model.addAttribute("user", user);
        return "job-post";
    }

    @PostMapping("job-post")
    public String saveJob(@ModelAttribute JobPost jobPost, Model model, HttpSession session) {
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

        if (jobPost.getId() != null && jobPostService.getJobPostByID(jobPost.getId()).isPresent()) {
            System.out.println("*********Present");
            LocalDateTime localDateTime = LocalDateTime.parse(jobPost.getTimestamp());
            jobPost.setClosingDate(Timestamp.valueOf(localDateTime));
            jobPost.setCreatedDate(Date.valueOf(LocalDate.now()));
            jobPost.setRecruiter(recruiterService.getRecruiterByEmail(user.getEmail()).get());

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
            jobPost.setRecruiter(recruiterService.getRecruiterByEmail(user.getEmail()).get());

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
        model.addAttribute("user", user);

        return "job-post";
    }

    @GetMapping("view-jobs")
    public String viewJobs(Model model, @RequestParam(value = "id", required = false) String id,
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

        if (id != null) {
            JobPost jobPost = jobPostService.getJobPostByID(Long.parseLong(id)).get();

            if (jobPostService.deleteJobPostById(Long.parseLong(id)))
                success = "Successfully deleted " + jobPost.getTitle();
            else
                error = "Failed to delete " + jobPost.getTitle();
        }

        Recruiter recruiter = recruiterService.getRecruiterByEmail(user.getEmail()).get();

        model.addAttribute("error", error);
        model.addAttribute("success", success);
        model.addAttribute("jobs", jobPostService.getByRecruiterId(recruiter.getId()));
        model.addAttribute("user", user);

        return "view-jobs";
    }

    @GetMapping("job")
    public String apply(Model model, @RequestParam(value = "id") String id, HttpSession session) {
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

        if (user.getRole().equalsIgnoreCase("recruiter")) {
            model.addAttribute("user", new User());
            model.addAttribute("success", "");
            model.addAttribute("error", "You are not authorized to access this page!");

            return "login";
        }

        CandidateAccount account = candidateAccountService.getUserAccountByEmail(user.getEmail()).get();

        if (!documentService.existsByCandidateEmailAndTitle(account.getEmail(), "CV")) {
            model.addAttribute("show", false);
            model.addAttribute("user", user);

            return "apply";
        }

        JobPost jobPost = jobPostService.getJobPostByID(Long.parseLong(id)).orElse(new JobPost());

        Candidate candidate = candidateService.getCandidateByEmail(user.getEmail()).get();

        boolean status = activityService.applied(jobPost.getId(), candidate.getId());

        model.addAttribute("show", true);
        model.addAttribute("status", status);
        model.addAttribute("jobPost", jobPost);
        model.addAttribute("user", user);

        return "apply";
    }

    @GetMapping("apply")
    public String applying(Model model, @RequestParam(value = "id") String id, HttpSession session) {
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

        if (user.getRole().equalsIgnoreCase("recruiter")) {
            model.addAttribute("user", new User());
            model.addAttribute("success", "");
            model.addAttribute("error", "You are not authorized to access this page!");

            return "login";
        }

        if (id != null) {
            JobPostActivity jobPostActivity = new JobPostActivity();
            jobPostActivity.setJobPost(jobPostService.getJobPostByID(Long.parseLong(id)).get());
            jobPostActivity.setCandidate(candidateService.getCandidateByEmail(user.getEmail()).get());
            jobPostActivity.setDate(Date.valueOf(LocalDate.now()));
            jobPostActivity.setStatus("IN PROGRESS");

            if (activityService.saveJobPostActivity(jobPostActivity)) {
                System.out.println("Applied for: " + jobPostService.getJobPostByID(Long.parseLong(id)).get().getTitle());

                model.addAttribute("user", user);

                return "thanks";
            }

            model.addAttribute("user", user);

            JobPost jobPost = jobPostService.getJobPostByID(Long.parseLong(id)).get();
            model.addAttribute("jobPost", jobPost);

            return "apply";
        }

        model.addAttribute("user", user);
        return "index";
    }
}