package com.dso34bt.jobportal.bootstrap;

import com.dso34bt.jobportal.services.DocumentService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;

@Component
public class Runner implements CommandLineRunner {
    private final DocumentService documentService;

    public Runner(DocumentService documentService) {
        this.documentService = documentService;
    }

    @Override
    public void run(String... args) throws Exception {
        /*File file = new File("/Users/alistair/Desktop/demo.pdf");
        try {
            FileOutputStream fout = new FileOutputStream(file);
            String s = "Example of Java program to write Bytes using ByteStream.";
            byte b[] = documentService.findById(3).get().getContent();
            fout.write(b);
            fout.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        PDDocument document = PDDocument.load(file);

        //Instantiate PDFTextStripper class
        PDFTextStripper pdfStripper = new PDFTextStripper();

        //Retrieving text from PDF document
        String text = pdfStripper.getText(document);
        System.out.println(text);

        //Closing the document
        document.close();*/
    }


}
