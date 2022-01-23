package com.dso34bt.jobportal.controllers;

import com.dso34bt.jobportal.model.*;
import com.dso34bt.jobportal.services.*;
import com.dso34bt.jobportal.utilities.Email;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Controller
public class ReviewController {
    private final CandidateService candidateService;
    private final QualificationService qualificationService;
    private final ExperienceService experienceService;
    private final DocumentService documentService;
    private final JobPostActivityService jobPostActivityService;
    private final RecruiterService recruiterService;
    private final RequestsService requestsService;
    private final CandidateEmailsService candidateEmailsService;
    private final RecruiterEmailsService recruiterEmailsService;

    public ReviewController(CandidateService candidateService, QualificationService qualificationService,
                            ExperienceService experienceService, DocumentService documentService,
                            JobPostActivityService jobPostActivityService, RecruiterService recruiterService,
                            RequestsService requestsService, CandidateEmailsService candidateEmailsService,
                            RecruiterEmailsService recruiterEmailsService) {
        this.candidateService = candidateService;
        this.qualificationService = qualificationService;
        this.experienceService = experienceService;
        this.documentService = documentService;
        this.jobPostActivityService = jobPostActivityService;
        this.recruiterService = recruiterService;
        this.requestsService = requestsService;
        this.candidateEmailsService = candidateEmailsService;
        this.recruiterEmailsService = recruiterEmailsService;
    }

    @GetMapping("review")
    public String review(Model model, @RequestParam(value = "id", required = false) String id,
                         @RequestParam(value = "action", required = false) String action,
                         @RequestParam(value = "file", required = false) String file,HttpSession session) {
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

        Candidate candidate = new Candidate();
        List<Qualifications> qualifications = new ArrayList<>();
        List<Experience> experiences = new ArrayList<>();
        List<Document> documents = new ArrayList<>();
        List<String> missingFiles = new ArrayList<>();
        List<String> requests = new ArrayList<>();

        if (user.getRole().equalsIgnoreCase("recruiter")) {
            if (action != null && action.equalsIgnoreCase("candidate") && id != null) {
                candidate = candidateService.findById(Long.parseLong(id)).get();
                qualifications = qualificationService.findByCandidateEmail(candidate.getCandidateAccount().getEmail());
                experiences = experienceService.findByCandidateEmail(candidate.getCandidateAccount().getEmail());
                documents = documentService.getCandidateDocuments(candidate.getCandidateAccount().getEmail());

                requests = requestsService.findTitlesByCandidateIdAndStatus(candidate.getId(), "Requested");

                List<String> titles = new ArrayList<>(Arrays.asList("CV", "ID Copy", "Academic Transcript", "Matric Results",
                        "Recommendation Letter", "Clearance Letter"));

                for (String title : titles){
                    if (!documentService.existsByCandidateEmailAndTitle(candidate.getCandidateAccount().getEmail(), title)){
                        missingFiles.add(title);
                    }
                }
                for (String request : requests){
                    missingFiles.remove(request);
                }

                model.addAttribute("requests",requests);
                model.addAttribute("candidate", candidate);
                model.addAttribute("qualifications", qualifications);
                model.addAttribute("experiences", experiences);
                model.addAttribute("documents", documents);
                model.addAttribute("missingFiles", missingFiles);
                model.addAttribute("error", error);
                model.addAttribute("success", success);
                model.addAttribute("user", user);

                return "review";
            }

            if (action != null && action.equalsIgnoreCase("request") && id != null) {
                candidate = candidateService.findById(Long.parseLong(id)).get();
                qualifications = qualificationService.findByCandidateEmail(candidate.getCandidateAccount().getEmail());
                experiences = experienceService.findByCandidateEmail(candidate.getCandidateAccount().getEmail());
                documents = documentService.getCandidateDocuments(candidate.getCandidateAccount().getEmail());

                // Request a files from candidate
                if (file != null){
                    Recruiter recruiter = recruiterService.getRecruiterByEmail(user.getEmail()).get();

                    Requests newRequest = new Requests();
                    newRequest.setId(requestsService.getLastId() + 1);
                    newRequest.setDocumentTitle(file);
                    newRequest.setStatus("Requested");
                    newRequest.setCandidate(candidate);
                    newRequest.setRecruiter(recruiterService.getRecruiterByEmail(user.getEmail()).get());

                    String to = candidate.getCandidateAccount().getEmail();
                    String subject = "MISSING DOCUMENTS";
                    String composedMessage = String.format("Dear %s \n\nYou are requested to upload '%s' into your portal. " +
                            "Failure to do so, we will not be able to proceed with any of your applications you submitted.\n\n" +
                            "Kind regards \n%s\n%s",candidate.getFirst_name(),file,recruiter.getFirstname() + " " +recruiter.getLastname(),
                            recruiter.getCompanyName());

                    // send candidate an email
                    if (Email.send(to,subject,composedMessage)){
                        CandidateEmails candidateEmail = new CandidateEmails();
                        candidateEmail.setId(candidateEmailsService.getLastId() + 1);
                        candidateEmail.setCandidateAccount(candidate.getCandidateAccount());
                        candidateEmail.setSenderEmail(candidate.getCandidateAccount().getEmail());
                        candidateEmail.setMessage(composedMessage);
                        candidateEmail.setSubject(subject);
                        candidateEmail.setTimeSent(Timestamp.valueOf(LocalDateTime.now()));

                        if (candidateEmailsService.save(candidateEmail))
                            System.out.println("Saved the email that was sent");

                        if (requestsService.save(newRequest))
                            success = "Successfully requested " + candidate.getFirst_name() + " to upload the required document";
                        else
                            error = "Failed to save the request!";
                    }
                    else
                        error = "Something went wrong while trying to send an email. Please try again later";

                }

                requests = requestsService.findTitlesByCandidateIdAndStatus(candidate.getId(), "Requested");


                List<String> titles = new ArrayList<>(Arrays.asList("CV", "ID Copy", "Academic Transcript", "Matric Results",
                        "Recommendation Letter", "Clearance Letter"));

                for (String title : titles){
                    if (!documentService.existsByCandidateEmailAndTitle(candidate.getCandidateAccount().getEmail(), title)){
                        missingFiles.add(title);
                    }
                }
                for (String request : requests){
                    missingFiles.remove(request);
                }

                model.addAttribute("requests",requests);
                model.addAttribute("candidate", candidate);
                model.addAttribute("qualifications", qualifications);
                model.addAttribute("experiences", experiences);
                model.addAttribute("documents", documents);
                model.addAttribute("missingFiles", missingFiles);
                model.addAttribute("error", error);

            }
            else {
                model.addAttribute("requests",requests);
                model.addAttribute("candidate", candidate);
                model.addAttribute("qualifications", qualifications);
                model.addAttribute("experiences", experiences);
                model.addAttribute("documents", documents);
                model.addAttribute("missingFiles", missingFiles);
                model.addAttribute("error", "Something went wrong while accessing the page! Please go back or try again later");

            }
            model.addAttribute("success", success);
            model.addAttribute("user", user);
            return "review";
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
