package com.dso34bt.jobportal.controllers;

import com.dso34bt.jobportal.model.*;
import com.dso34bt.jobportal.services.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class ReviewController {
    private final CandidateService candidateService;
    private final QualificationService qualificationService;
    private final ExperienceService experienceService;
    private final DocumentService documentService;
    private final JobPostActivityService jobPostActivityService;
    private final RecruiterService recruiterService;

    public ReviewController(CandidateService candidateService, QualificationService qualificationService,
                            ExperienceService experienceService, DocumentService documentService,
                            JobPostActivityService jobPostActivityService, RecruiterService recruiterService) {
        this.candidateService = candidateService;
        this.qualificationService = qualificationService;
        this.experienceService = experienceService;
        this.documentService = documentService;
        this.jobPostActivityService = jobPostActivityService;
        this.recruiterService = recruiterService;
    }

    @GetMapping("review")
    public String review(Model model, @RequestParam(value = "id", required = false) String id,
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

        String success = "";
        String error = "";

        if (user.getRole().equalsIgnoreCase("recruiter")) {
            if (action != null && action.equalsIgnoreCase("candidate") && id != null) {
                Candidate candidate = candidateService.findById(Long.parseLong(id)).get();
                List<Qualifications> qualifications = qualificationService.findByCandidateEmail(candidate.getCandidateAccount().getEmail());
                List<Experience> experiences = experienceService.findByCandidateEmail(candidate.getCandidateAccount().getEmail());
                List<Document> documents = documentService.getCandidateDocuments(candidate.getCandidateAccount().getEmail());

                List<String> missingFiles = new ArrayList<>();

                String[] titles = {"CV", "ID Copy", "Academic Transcript", "Matric Results", "Recommendation Letter", "Clearance Letter"};

                for (String title : titles){
                    if (!documentService.existsByCandidateEmailAndTitle(candidate.getCandidateAccount().getEmail(), title)){
                        missingFiles.add(title);
                    }
                }

                model.addAttribute("candidate", candidate);
                model.addAttribute("qualifications", qualifications);
                model.addAttribute("experiences", experiences);
                model.addAttribute("documents", documents);
                model.addAttribute("missingFiles", missingFiles);
                model.addAttribute("error", error);
                model.addAttribute("success", success);
                model.addAttribute("user", user);

                return "review";
            } else {
                Recruiter recruiter = recruiterService.getRecruiterByEmail(user.getEmail()).get();

                List<JobPostActivity> activityList = jobPostActivityService.findByJobPostRecruiterEmail(recruiter.getEmail());
                List<Applicant> applicants = new ArrayList<>();

                for (JobPostActivity activity : activityList)
                    applicants.add(new Applicant(activity.getCandidate(), activity.getJobPost(), activity));

                model.addAttribute("user", user);
                model.addAttribute("success", "");
                model.addAttribute("error", "Something went wrong while accessing the page!");
                model.addAttribute("applicants", applicants);

                return "applicants";
            }
        }


        model.addAttribute("error", error);
        model.addAttribute("success", success);
        model.addAttribute("user", user);

        return "review";
    }

    @GetMapping("file-download")
    public void downloadFiles(HttpServletResponse response, @RequestParam(value = "id") String id, Model model) {
        String success = "";
        String error = "";

        Optional<Document> result = documentService.findById(Long.parseLong(id));

        if (!result.isPresent())
            error = "Document not found!";
        else {
            Document document = result.get();
            response.setContentType("application/octet-stream");
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=" + document.getName();

            response.setHeader(headerKey, headerValue);

            try {
                ServletOutputStream outputStream = response.getOutputStream();
                outputStream.write(document.getContent());
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                error = "Failed to get file content!";
            }
        }
        model.addAttribute("success", success);
        model.addAttribute("error", error);
    }
}
