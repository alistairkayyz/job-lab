package com.dso34bt.jobportal.bootstrap;

import com.dso34bt.jobportal.model.Candidate;
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
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

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

    public Runner(DocumentService documentService, CandidateService candidateService,
                  QualificationService qualificationService,
                  ExperienceService experienceService) {
        this.documentService = documentService;
        this.candidateService = candidateService;
        this.qualificationService = qualificationService;
        this.experienceService = experienceService;
    }

    @Override
    public void run(String... args) throws Exception {
        /*String fileName = "alistair.pdf";

        PDDocument document = new PDDocument();

        PDPage page = new PDPage();

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        //Begin the Content stream
        contentStream.beginText();

        //Setting the font to the Content stream
        contentStream.setFont(PDType1Font.COURIER, 12);

        //Setting the leading
        contentStream.setLeading(14.5f);

        contentStream.setLineWidth(60);

        //Setting the position for the line
        contentStream.newLineAtOffset(20, 700);

        String text = "Hi!!! This is the multiple text content example.";
        String Line1 = "Here, we discussed about how to add text content in the pages of the PDF document.";
        String Line2 = "We do this by using the ShowText() method of the ContentStream class. We do this by using the ShowText() method of the ContentStream class";

        //Adding text in the form of string
        contentStream.showText(text);
        contentStream.newLine();
        contentStream.showText(Line1);
        contentStream.newLine();
        contentStream.showText(Line2);

        //Ending the content stream
        contentStream.endText();

        //Closing the content stream
        contentStream.close();

        document.addPage(page);
        document.save(fileName);

        System.out.println("PDF created");

        //Closing the document
        document.close();*/

       /* multiline();

        //Loading an existing document
        File file = new File("example.pdf");
        PDDocument doc = PDDocument.load(file);

        //Instantiate PDFTextStripper class
        PDFTextStripper pdfStripper = new PDFTextStripper();

        //Retrieving text from PDF document
        String txt = pdfStripper.getText(doc);
        System.out.println("Text in PDF\n---------------------------------");
        System.out.println(txt);

        //Closing the document
        doc.close();*/
    }

    public void multiline() {


        Optional<Candidate> optional = candidateService.getCandidateByEmail("jbeyer0@quantcast.com");

        Candidate candidate = optional.get();
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


            String text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt" +
                    " ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco" +
                    " laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in " +
                    " ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco" +
                    " laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in " +
                    "voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat" +
                    " non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";


            contentStream.beginText();

            // add heading
            contentStream.setFont(FONT_BOLD, FONT_SIZE + 4);
            addParagraph(contentStream, width, startX, startY, "Personal Information", false);
            contentStream.newLineAtOffset(0,LEADING);

            // add personal information
            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Title: " + candidate.getTitle());
            contentStream.newLineAtOffset(0,LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("First name: " + candidate.getFirst_name());
            contentStream.newLineAtOffset(0,LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Middle name: " + candidate.getMiddle_name());
            contentStream.newLineAtOffset(0,LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Last name: " + candidate.getLast_name());
            contentStream.newLineAtOffset(0,LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Date of birth: " + candidate.getDate_of_birth());
            contentStream.newLineAtOffset(0,LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Language: " + candidate.getLanguage());
            contentStream.newLineAtOffset(0,LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("ID number: " + candidate.getId_number());
            contentStream.newLineAtOffset(0,LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Do you have Driver's licence?: " + candidate.isDrivers_licence_valid());
            contentStream.newLineAtOffset(0,LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Race: " + candidate.getRace());
            contentStream.newLineAtOffset(0,LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Email: " + candidate.getCandidateAccount().getEmail());
            contentStream.newLineAtOffset(0,LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Telephone: " + candidate.getTelephone());
            contentStream.newLineAtOffset(0,LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Cellphone: " + candidate.getCellphone());
            contentStream.newLineAtOffset(0,LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Street Address: " + candidate.getStreet_address());
            contentStream.newLineAtOffset(0,LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Suburb: " + candidate.getSuburb());
            contentStream.newLineAtOffset(0,LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("City: " + candidate.getCity());
            contentStream.newLineAtOffset(0,LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Province: " + candidate.getProvince());
            contentStream.newLineAtOffset(0,LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Country: " + candidate.getCountry());
            contentStream.newLineAtOffset(0,LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.showText("Post Code: " + candidate.getPost_code());
            contentStream.newLineAtOffset(0,LEADING);

            contentStream.setFont(FONT, FONT_SIZE);
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

        java.util.List<String> lines = parseLines(text, width);

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

    private java.util.List<String> parseLines(String text, float width) throws IOException {
        java.util.List<String> lines = new ArrayList<>();
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
