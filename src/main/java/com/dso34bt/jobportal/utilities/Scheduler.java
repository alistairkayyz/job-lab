package com.dso34bt.jobportal.utilities;

import com.dso34bt.jobportal.model.*;
import com.dso34bt.jobportal.services.*;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class Scheduler {
    private final JobPostService jobPostService;
    private final JobPostActivityService jobPostActivityService;
    private final CandidateService candidateService;
    private final CandidateEmailsService candidateEmailsService;
    private final DocumentService documentService;
    private final RequestsService requestsService;
    private final RecruiterService recruiterService;
    private final RecruiterEmailsService recruiterEmailsService;
    private final QualificationService qualificationService;
    private final ExperienceService experienceService;

    public Scheduler(JobPostService jobPostService, JobPostActivityService jobPostActivityService,
                     CandidateService candidateService, CandidateEmailsService candidateEmailsService,
                     DocumentService documentService, RequestsService requestsService,
                     RecruiterService recruiterService, RecruiterEmailsService recruiterEmailsService,
                     QualificationService qualificationService, ExperienceService experienceService) {
        this.jobPostService = jobPostService;
        this.jobPostActivityService = jobPostActivityService;
        this.candidateService = candidateService;
        this.candidateEmailsService = candidateEmailsService;
        this.documentService = documentService;
        this.requestsService = requestsService;
        this.recruiterService = recruiterService;
        this.recruiterEmailsService = recruiterEmailsService;
        this.qualificationService = qualificationService;
        this.experienceService = experienceService;
    }

    @Scheduled(fixedRate = 1000)
    public void automaticProcessing() {
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        String to = "";
        String from = "joblab.tut@gmail.com";
        String subject = "";
        String composedMessage = "";

        // get job posts that have closed
        List<JobPost> jobPosts = jobPostService.getJobPostsAfterClosingDate(now);
        for (JobPost jobPost : jobPosts) {

            // get all the recruiters
            Recruiter recruiter = recruiterService.getRecruiterById(jobPost.getRecruiter().getId()).get();

            // get job post activities that were not processed
            List<JobPostActivity> activityList =
                    jobPostActivityService.findByJobPostIdAndStatus(jobPost.getId(), "IN PROGRESS");

            // get all the candidates
            List<Candidate> candidates = new ArrayList<>();
            for (JobPostActivity activity : activityList) {
                candidates.add(activity.getCandidate());
            }

            for (Candidate candidate : candidates) {
                // check if the candidate has any pending requests
                if (requestsService.findByCandidateIdAndStatus(candidate.getId(), "Requested").isEmpty()) {
                    try {
                        boolean hasQualification = false;
                        boolean hasExperience = false;

                        List<Qualifications> qualifications = qualificationService.findByCandidateEmail(candidate.getCandidateAccount().getEmail());
                        List<Experience> experiences = experienceService.findByCandidateEmail(candidate.getCandidateAccount().getEmail());

                        // check if the candidate has the qualification listed
                        for (Qualifications qualification : qualifications) {
                            String qualificationName = qualification.getQualificationName().toLowerCase(Locale.ROOT);

                            List<String> list = jobPost.getQualificationList();
                            for (String item : list) {
                                item = item.toLowerCase(Locale.ROOT);
                                if (qualificationName.endsWith(item)) {
                                    hasQualification = true;
                                    break;
                                }
                            }
                        }

                        // check if candidate has the experience listed
                        for (Experience experience : experiences) {
                            String title = experience.getJobTitle().toLowerCase(Locale.ROOT);
                            String description = experience.getDescription().toLowerCase(Locale.ROOT);

                            List<String> list = jobPost.getRequirementsList();
                            for (String item : list) {
                                item = item.toLowerCase(Locale.ROOT);
                                if (item.contains(title) || item.contains(description)) {
                                    hasExperience = true;
                                    break;
                                }
                            }
                        }

                        JobPostActivity activity = jobPostActivityService.findByCandidateIdAndJobPostId(candidate.getId(),
                                jobPost.getId()).get();

                        if (hasQualification && hasExperience) {

                            activity.setStatus("SUCCESSFUL");
                            if (jobPostActivityService.saveJobPostActivity(activity)) {
                                System.out.println("JobPostActivity was updated and " + candidate.getFirst_name() + "was successful");

                                // email to be sent to candidate
                                {
                                    to = candidate.getCandidateAccount().getEmail();
                                    subject = "YOUR JOB APPLICATION UPDATE";
                                    composedMessage = String.format("Hi %s,\n" +
                                                    "\n" +
                                                    "Thank you for your interest in our %s position and for your time and effort invested in " +
                                                    "completing your application. We are amazed with the great applications we have received" +
                                                    " – including yours! \n" +
                                                    "\n" +
                                                    "The competition for this position has been fierce. However, your application was successful " +
                                                    "and we would like to get to know you better. \n" +
                                                    " \n" +
                                                    "We will be in touch soon, and Congratulation!\n" +
                                                    "\n" +
                                                    "Kind regards,\n" +
                                                    "%s %s\n" +
                                                    "%s", candidate.getFirst_name(), jobPost.getTitle(), recruiter.getFirstname(),
                                            recruiter.getLastname(), recruiter.getCompanyName());
                                }

                                // send candidate an email
                                if (Email.send(to, subject, composedMessage)) {
                                    CandidateEmails candidateEmail = new CandidateEmails();
                                    candidateEmail.setId(candidateEmailsService.getLastId() + 1);
                                    candidateEmail.setCandidateAccount(candidate.getCandidateAccount());
                                    candidateEmail.setSenderEmail(from);
                                    candidateEmail.setMessage(composedMessage);
                                    candidateEmail.setSubject(subject);
                                    candidateEmail.setTimeSent(Timestamp.valueOf(LocalDateTime.now()));

                                    // email to send to a recruiter
                                    {
                                        to = candidate.getCandidateAccount().getEmail();
                                        subject = "SUCCESSFUL CANDIDATE";
                                        composedMessage = String.format("Hi %s,\n" +
                                                        "\n" +
                                                        "You are receiving this email because %s email: %s cellphone: %s was " +
                                                        "successful for %s position \n" +
                                                        "\n" +
                                                        "Kind regards,\n" +
                                                        "JobLab", recruiter.getFirstname(), candidate.getFirst_name() +
                                                        " " + candidate.getLast_name(), candidate.getCandidateAccount().getEmail(),
                                                candidate.getCellphone(), jobPost.getTitle());

                                        // send recruiter an email
                                        if (Email.send(to, subject, composedMessage)){
                                            RecruiterEmails email = new RecruiterEmails();
                                            email.setId(recruiterEmailsService.getLastId() + 1);
                                            email.setSenderEmail(from);
                                            email.setRecruiter(recruiter);
                                            email.setSenderEmail(subject);
                                            email.setMessage(composedMessage);
                                            email.setTimeSent(Timestamp.valueOf(LocalDateTime.now()));

                                            if (recruiterEmailsService.save(email))
                                                System.out.println("[RECRUITER] Saved the email that was sent to " + recruiter.getEmail());
                                        }
                                    }

                                    if (candidateEmailsService.save(candidateEmail))
                                        System.out.println("[CANDIDATE] Saved the email that was sent to " + candidate.getCandidateAccount().getEmail());
                                } else
                                    System.out.println("Something went wrong while trying to send an email. Please try again later");
                            } else
                                System.out.println("JobPostActivity was not updated for " + candidate.getFirst_name());
                        } else {

                            activity.setStatus("REGRETTED");
                            if (jobPostActivityService.saveJobPostActivity(activity)) {
                                System.out.println("JobPostActivity was updated and " + candidate.getFirst_name() + "was rejected");

                                to = candidate.getCandidateAccount().getEmail();
                                subject = "YOUR JOB APPLICATION UPDATE";
                                composedMessage = String.format("Hi %s,\n" +
                                                "\n" +
                                                "Thank you for your interest in our %s position and for your time and effort invested in " +
                                                "completing your application. We are amazed with the great applications we have received" +
                                                " – including yours! \n" +
                                                "\n" +
                                                "The competition for this position has been fierce. Unfortunately, we will not be able " +
                                                "to consider your application any further this time. We are certain that given your " +
                                                "attributes, you will be able to find a position that fits you better. \n" +
                                                " \n" +
                                                "Feel free to continue exploring JobLab career site, and hope that you find a more " +
                                                "suitable future opportunity!\n" +
                                                "\n" +
                                                "Kind regards,\n" +
                                                "%s %s\n" +
                                                "%s", candidate.getFirst_name(), jobPost.getTitle(), recruiter.getFirstname(),
                                        recruiter.getLastname(), recruiter.getCompanyName());

                                // send candidate an email
                                if (Email.send(to, subject, composedMessage)) {
                                    CandidateEmails candidateEmail = new CandidateEmails();
                                    candidateEmail.setId(candidateEmailsService.getLastId() + 1);
                                    candidateEmail.setCandidateAccount(candidate.getCandidateAccount());
                                    candidateEmail.setSenderEmail(from);
                                    candidateEmail.setMessage(composedMessage);
                                    candidateEmail.setSubject(subject);
                                    candidateEmail.setTimeSent(Timestamp.valueOf(LocalDateTime.now()));

                                    if (candidateEmailsService.save(candidateEmail))
                                        System.out.println("Saved the email that was sent");
                                } else
                                    System.out.println("Something went wrong while trying to send an email. Please try again later");
                            } else
                                System.out.println("JobPostActivity was not updated for " + candidate.getFirst_name());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // regret and send an email
                else {
                    JobPostActivity activity = jobPostActivityService.findByCandidateIdAndJobPostId(candidate.getId(),
                            jobPost.getId()).get();

                    activity.setStatus("REGRETTED");
                    if (jobPostActivityService.saveJobPostActivity(activity)) {
                        System.out.println("JobPostActivity was updated and " + candidate.getFirst_name() + "was rejected");

                        to = candidate.getCandidateAccount().getEmail();
                        subject = "YOUR JOB APPLICATION UPDATE";
                        composedMessage = String.format("Hi %s,\n" +
                                        "\n" +
                                        "Thank you for your interest in our %s position and for your time and effort invested in " +
                                        "completing your application. We are amazed with the great applications we have received" +
                                        " – including yours! \n" +
                                        "\n" +
                                        "The competition for this position has been fierce. Unfortunately, we will not be able " +
                                        "to consider your application any further this time. We are certain that given your " +
                                        "attributes, you will be able to find a position that fits you better. \n" +
                                        " \n" +
                                        "Feel free to continue exploring JobLab career site, and hope that you find a more " +
                                        "suitable future opportunity!\n" +
                                        "\n" +
                                        "Kind regards,\n" +
                                        "%s %s\n" +
                                        "%s", candidate.getFirst_name(), jobPost.getTitle(), recruiter.getFirstname(),
                                recruiter.getLastname(), recruiter.getCompanyName());

                        // send candidate an email
                        if (Email.send(to, subject, composedMessage)) {
                            CandidateEmails candidateEmail = new CandidateEmails();
                            candidateEmail.setId(candidateEmailsService.getLastId() + 1);
                            candidateEmail.setCandidateAccount(candidate.getCandidateAccount());
                            candidateEmail.setSenderEmail(from);
                            candidateEmail.setMessage(composedMessage);
                            candidateEmail.setSubject(subject);
                            candidateEmail.setTimeSent(Timestamp.valueOf(LocalDateTime.now()));

                            if (candidateEmailsService.save(candidateEmail))
                                System.out.println("Saved the email that was sent");
                        } else
                            System.out.println("Something went wrong while trying to send an email. Please try again later");
                    } else
                        System.out.println("JobPostActivity was not updated for " + candidate.getFirst_name());


                }
            }
        }
    }
}
