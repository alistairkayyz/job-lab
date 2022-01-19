package com.dso34bt.jobportal.controllers;

import com.dso34bt.jobportal.model.*;
import com.dso34bt.jobportal.services.CandidateService;
import com.dso34bt.jobportal.services.DocumentService;
import com.dso34bt.jobportal.services.ExperienceService;
import com.dso34bt.jobportal.services.QualificationService;
import com.dso34bt.jobportal.utilities.Session;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class FilesController {
    final PDFont FONT = PDType1Font.HELVETICA;
    final float FONT_SIZE = 12;
    final float LEADING = -1.5f * FONT_SIZE;
    private final DocumentService documentService;
    private final CandidateService candidateService;
    private final ExperienceService experienceService;
    private final QualificationService qualificationService;

    public FilesController(DocumentService documentService,
                           CandidateService candidateService,
                           ExperienceService experienceService,
                           QualificationService qualificationService) {
        this.documentService = documentService;
        this.candidateService = candidateService;
        this.experienceService = experienceService;
        this.qualificationService = qualificationService;
    }

    @GetMapping("files")
    public String files(Model model, @RequestParam(value = "id", required = false) String id,
                        @RequestParam(value = "action", required = false) String action){
        if (Session.getCandidateAccount() == null) {
            model.addAttribute("user", new CandidateAccount());

            return "login";
        }
        CandidateAccount account = Session.getCandidateAccount();
        List<Document> documents = new ArrayList<>();

        String success = "";
        StringBuilder error = new StringBuilder();

        if (id != null && action != null){

            // Generate CV
            if (id.equalsIgnoreCase("cv") && action.equalsIgnoreCase("generate")){
                List<String> list = new ArrayList<>();
                if (!candidateService.candidateExists(account.getEmail()))
                    list.add("PROFILE");

                if (!qualificationService.existsByEmail(account.getEmail()))
                    list.add("QUALIFICATION");

                if (!experienceService.existsByCandidateEmail(account.getEmail()))
                    list.add("EXPERIENCE");

                if (list.isEmpty())
                    success = "CV was successfully generated";
                else{
                    error = new StringBuilder("Please complete: ");
                    for (String text : list)
                        error.append("[ ").append(text).append(" ] ");
                }
            }

            // delete the specified file
            if (!id.equalsIgnoreCase("cv") && action.equalsIgnoreCase("delete")){
                Document document = documentService.findById(Long.parseLong(id)).get();

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
        model.addAttribute("user", Session.getCandidateAccount());

        return "files";
    }

    @PostMapping("files")
    public String storeFiles(@ModelAttribute Upload file, Model model){
        if (Session.getCandidateAccount() == null) {
            model.addAttribute("user", new CandidateAccount());

            return "login";
        }

        CandidateAccount account = Session.getCandidateAccount();

        Document document = new Document();

        try {
            List<Document> documents = documentService.getCandidateDocuments(account.getEmail());

            // set new document id
            document.setId(documentService.getLastId() + 1);

            // get existing document id and set it to update the existing doc
            for (Document doc : documents){
                if (documentService.existsByCandidateEmailAndTitle(account.getEmail(), file.getTitle())){
                    document.setId(doc.getId());
                }
            }

            document.setTitle(file.getTitle());
            document.setName(StringUtils.cleanPath(Objects.requireNonNull(file.getFile().getOriginalFilename())));

            if (file.getFile().getSize() < 4000000)
                document.setSize(file.getFile().getSize());
            else {
                model.addAttribute("success", "");
                model.addAttribute("error", "ERROR: File size exceeds 4MB");
                model.addAttribute("documents", documentService.getCandidateDocuments(account.getEmail()));
                model.addAttribute("upload", new Upload());
                model.addAttribute("user", Session.getCandidateAccount());
                return "files";
            }

            document.setContent(file.getFile().getBytes());
            document.setCandidateAccount(account);
        }
        catch (Exception e){
            System.err.println(e.getMessage());
            model.addAttribute("success", "");
            model.addAttribute("error", "ERROR: Failed to upload the file, check the log for more information.");
            model.addAttribute("documents", documentService.getCandidateDocuments(account.getEmail()));
            model.addAttribute("upload", new Upload());
            model.addAttribute("user", Session.getCandidateAccount());
            return "files";
        }

        // save a new document or update
        if (documentService.saveDocument(document)){
            model.addAttribute("success", "Successfully uploaded a new file");
            model.addAttribute("error", "");
        }
        else {
            model.addAttribute("success", "");
            model.addAttribute("error", "ERROR: Failed to upload the file, check the log for more information.");
        }

        model.addAttribute("documents", documentService.getCandidateDocuments(account.getEmail()));
        model.addAttribute("upload", new Upload());
        model.addAttribute("user", Session.getCandidateAccount());
        return "files";
    }

    @GetMapping("download")
    public void downloadFiles(HttpServletResponse response, @RequestParam(value = "id") String id, Model model){
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

            response.setHeader(headerKey,headerValue);

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
        model.addAttribute("error",error);
    }

    public void multiline(String text) {
        try (final PDDocument doc = new PDDocument()) {

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
            addParagraph(contentStream, width, startX, startY, text, false);
            //contentStream.setFont(FONT, FONT_SIZE);
            //contentStream.showText("Start here...");
            addParagraph(contentStream, width, 0, -FONT_SIZE, text, false);
            contentStream.endText();

            contentStream.close();

            doc.save("example.pdf");
        }
        catch (IOException e) {
            System.err.println("Exception while trying to create pdf document - " + e);
        }
    }

    private void addParagraph(PDPageContentStream contentStream, float width, float sx,
                              float sy, String text, boolean justify) throws IOException {

        List<String> lines = parseLines(text, width);
        contentStream.setFont(FONT, FONT_SIZE);
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
            }
            else if (spaceIndex == text.length()) {
                lines.add(text);
                text = "";
            }
            else {
                lastSpace = spaceIndex;
            }
        }
        return lines;
    }
}
