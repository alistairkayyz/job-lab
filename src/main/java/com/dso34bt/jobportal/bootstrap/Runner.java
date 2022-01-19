package com.dso34bt.jobportal.bootstrap;

import com.dso34bt.jobportal.services.DocumentService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
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
        String fileName = "alistair.pdf";

        //PDDocument document = new PDDocument();

        File file = new File("alistair.pdf");
        PDDocument document = PDDocument.load(file);

        PDPage page =document.getPage(1);
        PDPageContentStream contentStream = new PDPageContentStream(document,page);

        //Begin the Content stream
        contentStream.beginText();

        //Setting the font to the Content stream
        contentStream.setFont(PDType1Font.COURIER, 14);

        //Setting the position for the line
        contentStream.newLineAtOffset(20, 450);

        String text = "Hi!!! This is the first sample PDF document.";

        //Adding text in the form of string
        contentStream.showText(text);

        //Ending the content stream
        contentStream.endText();

        //Closing the content stream
        contentStream.close();

        document.save(fileName);

        System.out.println("PDF created");

        //Closing the document
        document.close();
    }


}
