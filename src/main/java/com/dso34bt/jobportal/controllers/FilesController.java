package com.dso34bt.jobportal.controllers;

import com.dso34bt.jobportal.model.*;
import com.dso34bt.jobportal.services.*;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class FilesController {
    final PDFont FONT = PDType1Font.HELVETICA;
    final PDFont FONT_BOLD = PDType1Font.HELVETICA_BOLD;
    final float FONT_SIZE = 12;
    final float LEADING = -1.5f * FONT_SIZE;

    private final DocumentService documentService;
    private final CandidateService candidateService;
    private final ExperienceService experienceService;
    private final QualificationService qualificationService;
    private final CandidateAccountService candidateAccountService;
    private final RequestsService requestsService;
    private final RecruiterEmailsService recruiterEmailsService;

    public FilesController(DocumentService documentService, CandidateService candidateService,
                           ExperienceService experienceService, QualificationService qualificationService,
                           CandidateAccountService candidateAccountService, RequestsService requestsService,
                           RecruiterEmailsService recruiterEmailsService) {
        this.documentService = documentService;
        this.candidateService = candidateService;
        this.experienceService = experienceService;
        this.qualificationService = qualificationService;
        this.candidateAccountService = candidateAccountService;
        this.requestsService = requestsService;
        this.recruiterEmailsService = recruiterEmailsService;
    }

    @GetMapping("files")
    public String files(Model model, @RequestParam(value = "id", required = false) String id,
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

        if (user.getRole().equalsIgnoreCase("recruiter")){
            model.addAttribute("user", new User());
            model.addAttribute("success", "");
            model.addAttribute("error", "You are not authorized to access this page!");

            return "login";
        }

        CandidateAccount account = candidateAccountService.getUserAccountByEmail(user.getEmail()).get();
        Candidate candidate = candidateService.getCandidateByEmail(account.getEmail()).orElse(new Candidate());
        List<Document> documents = new ArrayList<>();

        String success = "";
        StringBuilder error = new StringBuilder();

        if (id != null && action != null) {

            // Generate CV
            if (id.equalsIgnoreCase("cv") && action.equalsIgnoreCase("generate")) {
                List<String> list = new ArrayList<>();
                if (!candidateService.candidateExists(account.getEmail()))
                    list.add("PROFILE");

                if (!qualificationService.existsByEmail(account.getEmail()))
                    list.add("QUALIFICATION");

                if (!experienceService.existsByCandidateEmail(account.getEmail()))
                    list.add("EXPERIENCE");

                if (list.isEmpty()) {
                    try {
                        String filename = "files/"+ candidate.getFirst_name()+ "_" + candidate.getLast_name() +"_cv"+ System.currentTimeMillis() +".pdf";
                        generateCV(account, filename);

                        //Loading an existing document
                        File file = new File(filename);

                        FileInputStream input = new FileInputStream(file);
                        MultipartFile multipartFile = new MockMultipartFile("file",
                                file.getName(), "text/plain", IOUtils.toByteArray(input));

                        Document document = new Document();

                        try {
                            documents = documentService.getCandidateDocuments(account.getEmail());

                            // set new document id
                            document.setId(documentService.getLastId() + 1);

                            // get existing document id and set it to update the existing doc
                            for (Document doc : documents) {
                                if (documentService.existsByCandidateEmailAndTitle(account.getEmail(), "CV")) {
                                    document.setId(doc.getId());
                                }
                            }

                            document.setTitle("CV");
                            document.setName(StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename())));

                            if (multipartFile.getSize() < 4000000)
                                document.setSize(multipartFile.getSize());
                            else {
                                model.addAttribute("success", "");
                                model.addAttribute("error", "ERROR: File size exceeds 4MB");
                                model.addAttribute("documents", documentService.getCandidateDocuments(account.getEmail()));
                                model.addAttribute("upload", new Upload());
                                model.addAttribute("user", user);
                                return "files";
                            }

                            document.setContent(multipartFile.getBytes());
                            document.setCandidateAccount(account);

                            // save a new document or update
                            if (documentService.saveDocument(document)) {
                                success = "CV was successfully generated and saved";
                            }
                            else {
                                error = new StringBuilder("ERROR: Failed to upload the file, check the log for more information.");
                            }
                        }
                        catch (Exception e) {
                            System.err.println(e.getMessage());
                            error = new StringBuilder("ERROR: Failed to upload the file, check the log for more information.");
                        }
                    }
                    catch (Exception exception) {
                        error = new StringBuilder(exception.getMessage());
                    }
                }
                else {
                    error = new StringBuilder("Please complete: ");
                    for (String text : list)
                        error.append("[ ").append(text).append(" ] ");
                }
            }

            // delete the specified file
            if (!id.equalsIgnoreCase("cv") && action.equalsIgnoreCase("delete")) {
                Document document = documentService.findById(Long.parseLong(id)).orElse(new Document());

                if (documentService.deleteByEntity(document))
                    success = "Successfully deleted '" + document.getTitle() + "'";
                else
                    error = new StringBuilder("ERROR: Failed to delete '" + document.getTitle() + "'");
            }
        }


        if (documentService.existsByCandidateEmail(account.getEmail()))
            documents = documentService.getCandidateDocuments(account.getEmail());

        model.addAttribute("success", success);
        model.addAttribute("error", error.toString());
        model.addAttribute("documents", documents);
        model.addAttribute("upload", new Upload());
        model.addAttribute("user", user);

        return "files";
    }

    @PostMapping("files")
    public String storeFiles(@ModelAttribute Upload file, Model model, HttpSession session) {
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

        CandidateAccount account = candidateAccountService.getUserAccountByEmail(user.getEmail()).get();

        Document document = new Document();

        try {
            List<Document> documents = documentService.getCandidateDocuments(account.getEmail());

            // set new document id
            document.setId(documentService.getLastId() + 1);

            // get existing document id and set it to update the existing doc
            for (Document doc : documents) {
                if (documentService.existsByCandidateEmailAndTitle(account.getEmail(), file.getTitle())) {
                    document.setId(doc.getId());
                }
            }

            document.setTitle(file.getTitle());

            String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getFile().getOriginalFilename()));

            if (filename.endsWith("pdf") || filename.endsWith("docx"))
                document.setName(filename);
            else {
                model.addAttribute("success", "");
                model.addAttribute("error", "ERROR: Invalid file format!");
                model.addAttribute("documents", documentService.getCandidateDocuments(account.getEmail()));
                model.addAttribute("upload", new Upload());
                model.addAttribute("user", user);
                return "files";
            }

            if (file.getFile().getSize() < 4000000)
                document.setSize(file.getFile().getSize());
            else {
                model.addAttribute("success", "");
                model.addAttribute("error", "ERROR: File size exceeds 4MB");
                model.addAttribute("documents", documentService.getCandidateDocuments(account.getEmail()));
                model.addAttribute("upload", new Upload());
                model.addAttribute("user", user);
                return "files";
            }

            document.setContent(file.getFile().getBytes());
            document.setCandidateAccount(account);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            model.addAttribute("success", "");
            model.addAttribute("error", "ERROR: Failed to upload the file, check the log for more information.");
            model.addAttribute("documents", documentService.getCandidateDocuments(account.getEmail()));
            model.addAttribute("upload", new Upload());
            model.addAttribute("user", user);
            return "files";
        }

        // save a new document or update
        if (documentService.saveDocument(document)) {
            model.addAttribute("success", "Successfully uploaded a new file");
            model.addAttribute("error", "");
        } else {
            model.addAttribute("success", "");
            model.addAttribute("error", "ERROR: Failed to upload the file, check the log for more information.");
        }

        model.addAttribute("documents", documentService.getCandidateDocuments(account.getEmail()));
        model.addAttribute("upload", new Upload());
        model.addAttribute("user", user);
        return "files";
    }

    @GetMapping("download")
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

    public void generateCV(CandidateAccount account, String filename) throws Exception {

        Optional<Candidate> optional = candidateService.getCandidateByEmail(account.getEmail());
        List<Qualifications> qualifications = qualificationService.findByCandidateEmail(account.getEmail());
        List<Experience> experiences = experienceService.findByCandidateEmail(account.getEmail());

        Candidate candidate = optional.orElse(new Candidate());
        PDDocument doc = new PDDocument();

        PDPage page = new PDPage();
        doc.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(doc, page);

        PDRectangle mediaBox = page.getMediaBox();
        float marginY = 80;
        float marginX = 60;
        float width = mediaBox.getWidth() - 2 * marginX;
        float startX = mediaBox.getLowerLeftX() + marginX;
        float startY = mediaBox.getUpperRightY() - marginY;

        contentStream.beginText();

        {
            // add heading
            contentStream.setFont(FONT_BOLD, FONT_SIZE + 4);
            addParagraph(contentStream, width, startX, startY, "Personal Information", false);
            contentStream.newLineAtOffset(0, LEADING);

            // add personal information
            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Title: " + candidate.getTitle());
            contentStream.newLineAtOffset(0, LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("First name: " + candidate.getFirst_name());
            contentStream.newLineAtOffset(0, LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Middle name: " + candidate.getMiddle_name());
            contentStream.newLineAtOffset(0, LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Last name: " + candidate.getLast_name());
            contentStream.newLineAtOffset(0, LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Date of birth: " + candidate.getDate_of_birth());
            contentStream.newLineAtOffset(0, LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Language: " + candidate.getLanguage());
            contentStream.newLineAtOffset(0, LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("ID number: " + candidate.getId_number());
            contentStream.newLineAtOffset(0, LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Do you have Driver's licence?: " + candidate.isDrivers_licence_valid());
            contentStream.newLineAtOffset(0, LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Race: " + candidate.getRace());
            contentStream.newLineAtOffset(0, LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Email: " + candidate.getCandidateAccount().getEmail());
            contentStream.newLineAtOffset(0, LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Telephone: " + candidate.getTelephone());
            contentStream.newLineAtOffset(0, LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Cellphone: " + candidate.getCellphone());
            contentStream.newLineAtOffset(0, LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Street Address: " + candidate.getStreet_address());
            contentStream.newLineAtOffset(0, LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Suburb: " + candidate.getSuburb());
            contentStream.newLineAtOffset(0, LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("City: " + candidate.getCity());
            contentStream.newLineAtOffset(0, LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Province: " + candidate.getProvince());
            contentStream.newLineAtOffset(0, LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Country: " + candidate.getCountry());
            contentStream.newLineAtOffset(0, LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Post Code: " + candidate.getPost_code());
            contentStream.newLineAtOffset(0, LEADING);
            contentStream.newLineAtOffset(0, LEADING);
        }

        // add qualifications
        contentStream.setFont(FONT_BOLD, FONT_SIZE + 4);
        addParagraph(contentStream, width, 0, -FONT_SIZE, "Qualifications", false);
        contentStream.newLineAtOffset(0, LEADING);

        for (Qualifications qualification : qualifications) {
            contentStream.setFont(FONT_BOLD, FONT_SIZE);
            contentStream.showText("Institution name: " + qualification.getInstitutionName());
            contentStream.newLineAtOffset(0, LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Qualification name: " + qualification.getQualificationName());
            contentStream.newLineAtOffset(0, LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Major: " + qualification.getMajor());
            contentStream.newLineAtOffset(0, LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Aggregate: " + qualification.getAggregate());
            contentStream.newLineAtOffset(0, LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Start date: " + qualification.getStartDate());
            contentStream.newLineAtOffset(0, LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Completion date: " + qualification.getCompletionDate());
            contentStream.newLineAtOffset(0, LEADING);
            contentStream.newLineAtOffset(0, LEADING);
        }
        contentStream.endText();
        contentStream.close();

        PDPage page2 = new PDPage();
        doc.addPage(page2);
        contentStream = new PDPageContentStream(doc, doc.getPage(1));

        contentStream.beginText();

        // add experience
        contentStream.setFont(FONT_BOLD, FONT_SIZE + 4);
        addParagraph(contentStream, width, startX, startY, "Experience", true);
        contentStream.newLineAtOffset(0, LEADING);

        for (Experience experience : experiences) {
            contentStream.setFont(FONT_BOLD, FONT_SIZE);
            contentStream.showText("Company name: " + experience.getCompanyName());
            contentStream.newLineAtOffset(0, LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Current job: " + experience.isCurrent_job());
            contentStream.newLineAtOffset(0, LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Job title: " + experience.getJobTitle());
            contentStream.newLineAtOffset(0, LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            addParagraph(contentStream, width, 0, -FONT_SIZE, "Description: " + experience.getDescription(), false);
            contentStream.newLineAtOffset(0, LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Start date: " + experience.getStartDate());
            contentStream.newLineAtOffset(0, LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("End date: " + experience.getEndDate());
            contentStream.newLineAtOffset(0, LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Notice period: " + experience.getNoticePeriod());
            contentStream.newLineAtOffset(0, LEADING);
            contentStream.newLineAtOffset(0, LEADING);
        }
        contentStream.endText();
        contentStream.close();


        doc.save(filename);

    }

    private void addParagraph(PDPageContentStream contentStream, float width, float sx,
                              float sy, String text, boolean justify) throws IOException {

        List<String> lines = parseLines(text, width);

        contentStream.newLineAtOffset(sx, sy);
        for (String line : lines) {
            float charSpacing = 0;
            if (justify) {
                if (line.length() > 1) {
                    float size = FONT_SIZE * FONT.getStringWidth(line) / 1000;
                    float free = width - size;
                    if (free > 0 && !lines.get(lines.size() - 1).equals(line)) {
                        charSpacing = free / (line.length() - 1);
                    }
                }
            }
            contentStream.setCharacterSpacing(charSpacing);
            contentStream.showText(line);
            contentStream.newLineAtOffset(0, LEADING);
        }
    }

    private List<String> parseLines(String text, float width) throws IOException {
        List<String> lines = new ArrayList<>();
        int lastSpace = -1;
        while (text.length() > 0) {
            int spaceIndex = text.indexOf(' ', lastSpace + 1);
            if (spaceIndex < 0)
                spaceIndex = text.length();
            String subString = text.substring(0, spaceIndex);
            float size = FONT_SIZE * FONT.getStringWidth(subString) / 1000;
            if (size > width) {
                if (lastSpace < 0) {
                    lastSpace = spaceIndex;
                }
                subString = text.substring(0, lastSpace);
                lines.add(subString);
                text = text.substring(lastSpace).trim();
                lastSpace = -1;
            } else if (spaceIndex == text.length()) {
                lines.add(text);
                text = "";
            } else {
                lastSpace = spaceIndex;
            }
        }
        return lines;
    }
}