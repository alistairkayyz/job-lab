package com.dso34bt.jobportal.bootstrap;

import com.dso34bt.jobportal.model.Candidate;
import com.dso34bt.jobportal.model.Experience;
import com.dso34bt.jobportal.model.Qualifications;
import com.dso34bt.jobportal.services.CandidateService;
import com.dso34bt.jobportal.services.DocumentService;
import com.dso34bt.jobportal.services.ExperienceService;
import com.dso34bt.jobportal.services.QualificationService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Component
public class Runner implements CommandLineRunner {
    final PDFont FONT = PDType1Font.HELVETICA;
    final PDFont FONT_BOLD = PDType1Font.HELVETICA_BOLD;
    final float FONT_SIZE = 12;
    final float LEADING = -1.5f * FONT_SIZE;

    private final DocumentService documentService;
    private final CandidateService candidateService;
    private final QualificationService qualificationService;
    private final ExperienceService experienceService;
    private final JavaMailSender javaMailSender;

    @PersistenceContext
    private EntityManager em;

    public Runner(DocumentService documentService, CandidateService candidateService,
                  QualificationService qualificationService,
                  ExperienceService experienceService, JavaMailSender javaMailSender) {
        this.documentService = documentService;
        this.candidateService = candidateService;
        this.qualificationService = qualificationService;
        this.experienceService = experienceService;
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void run(String... args) {

        System.out.println("Sending Email...");
        Properties props = new Properties();
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "*");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("joblab.tut@gmail.com", "awjgwonjgdklogmq");
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress( "no-reply@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse("Percythulani48@gmail.com"));
            message.setSubject("Blackino");
            message.setText("Dear Blackino,"
                    + "\n\n No spam to my email, please! thi is just a test");

            //Transport.send(message);

            System.out.println("Done");

        }
        catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

    @Scheduled(cron = "0 0 12 * * ?")
    public void sendMail() {
        // Your mail logic will go here
        System.out.println("Scheduled task running");

    }

    public void hello() {

        TypedQuery<Candidate> query = em.createQuery("SELECT e FROM Candidate e where e.country LIKE '%states%'", Candidate.class);
        List<Candidate> candidates = query.getResultList();

        for (Candidate candidate : candidates)
            System.out.println(candidate.getLast_name());
    }
}
