package com.dso34bt.jobportal.controllers;

import com.dso34bt.jobportal.model.*;
import com.dso34bt.jobportal.services.CandidateService;
import com.dso34bt.jobportal.services.DocumentService;
import com.dso34bt.jobportal.utilities.Session;
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
    private final DocumentService documentService;
    private final CandidateService candidateService;

    public FilesController(DocumentService documentService, CandidateService candidateService) {
        this.documentService = documentService;
        this.candidateService = candidateService;
    }

    @GetMapping("files")
    public String files(Model model, @RequestParam(value = "id", required = false) String id){
        if (Session.getCandidateAccount() == null) {
            model.addAttribute("user", new CandidateAccount());

            return "login";
        }
        CandidateAccount account = Session.getCandidateAccount();
        List<Document> documents = new ArrayList<>();

        String success = "";
        String error = "";

        if (id != null){

            // delete all files
            if (id.equalsIgnoreCase("all")){
                List<Document> documentList = documentService.getCandidateDocuments(account.getEmail());

                for (Document document : documentList){
                    if (documentService.deleteByEntity(document)){
                        error  ="ERROR: Failed to delete '" + document.getTitle() + "'";
                        break;
                    }
                }

                if (!documentService.existsByCandidateEmail(account.getEmail())){
                    success = "Successfully deleted all files";
                }
            }

            // delete the specified file
            if (!id.equalsIgnoreCase("all")){
                Document document = documentService.findById(Long.parseLong(id)).get();

                if (documentService.deleteByEntity(document))
                    error  ="ERROR: Failed to delete '" + document.getTitle() + "'";
                else
                    success = "Successfully deleted '" + document.getTitle() + "'";
            }
        }


        if (documentService.existsByCandidateEmail(account.getEmail()))
            documents = documentService.getCandidateDocuments(account.getEmail());

        model.addAttribute("success", success);
        model.addAttribute("error", error);
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

        if (!documentService.existsByCandidateEmailAndTitle(account.getEmail(), "CV")
                && !file.getTitle().equalsIgnoreCase("cv")){
            model.addAttribute("success", "");
            model.addAttribute("error", "ERROR: You must upload a 'CV' first.");
            model.addAttribute("documents", documentService.getCandidateDocuments(account.getEmail()));
            model.addAttribute("upload", new Upload());
            model.addAttribute("user", Session.getCandidateAccount());
            return "files";
        }

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
}
