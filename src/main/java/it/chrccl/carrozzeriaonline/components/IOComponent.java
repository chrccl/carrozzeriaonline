package it.chrccl.carrozzeriaonline.components;

import it.chrccl.carrozzeriaonline.model.Constants;
import it.chrccl.carrozzeriaonline.model.bot.BotContext;
import it.chrccl.carrozzeriaonline.model.dao.Partner;
import it.chrccl.carrozzeriaonline.model.dao.RepairCenter;
import it.chrccl.carrozzeriaonline.model.dao.TaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class IOComponent {

    public void writeOnWarrantFile(BotContext context, Partner partner, String text, int x, int y){
        String templatePdfPath = partner == Partner.CARLINK
                ? Constants.CARLINK_WARRANT_PATH
                : Constants.SAVOIA_WARRANT_PATH;

        String outputPdfPath = partner == Partner.CARLINK
                ? String.format(
                    Constants.USER_CARLINK_WARRANT_PATH_FORMAT,
                    context.getTask().getUser().getMobilePhone() + "_" +
                            context.getTask().getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                )
                : String.format(
                    Constants.USER_SAVOIA_WARRANT_PATH_FORMAT,
                    context.getTask().getUser().getMobilePhone() + "_" +
                        context.getTask().getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                );

        log.debug("Writing text on PDF: {} at position ({}, {})", outputPdfPath, x, y);

        if (context.getTask().getStatus() == TaskStatus.FULL_NAME) {
            copyAndRenameFile(templatePdfPath, outputPdfPath);
        }
        try (PDDocument document = PDDocument.load(new File(outputPdfPath))) {
            PDPage page = document.getPage(0);
            try (PDPageContentStream contentStream =
                         new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                writeText(contentStream, text, x, y);
            }
            document.save(outputPdfPath);
            log.debug("Text added and PDF saved at: {}", outputPdfPath);
        } catch (IOException e) {
            log.error("Error while processing PDF: {}", String.valueOf(e.getCause()));
        }
    }

    public void signWarrantFile(BotContext context, Partner partner, String text, int x, int y) {
        try {
            String outputPdfPath = partner == Partner.CARLINK
                ? String.format(
                    Constants.USER_CARLINK_WARRANT_PATH_FORMAT,
                    context.getTask().getUser().getMobilePhone() + "_" +
                            context.getTask().getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                )
                : String.format(
                    Constants.USER_SAVOIA_WARRANT_PATH_FORMAT,
                    context.getTask().getUser().getMobilePhone() + "_" +
                            context.getTask().getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                );

            PDDocument document = PDDocument.load(new File(outputPdfPath));
            PDPage page = document.getPage(0);

            File fontFile = new File(Constants.FONT_PATH);
            PDType0Font font = PDType0Font.load(document, fontFile);

            try (PDPageContentStream contentStream =
                         new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                contentStream.beginText();
                contentStream.setFont(font, 36);
                contentStream.newLineAtOffset(x, y);
                contentStream.showText(text);
                contentStream.endText();
            }
            // Save the signed PDF
            document.save(outputPdfPath);
            log.debug("Text added and PDF saved at: {}", outputPdfPath);
        }catch (IOException e) {
            log.error("Error while processing PDF: {}", String.valueOf(e.getCause()));
        }
    }

    public void removeTmpLogs(String fromNumber) {
        Path dir = Paths.get(String.format(Constants.USER_WARRANT_DIRECTORY_PATH_FORMAT, fromNumber));

        try {
            Files.walkFileTree(dir, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });

            log.info("Directory and all contents deleted");
        } catch (IOException e) {
            log.error("Failed to delete directory: {}", e.getMessage());
        }
    }

    public void addToReportNoleggioSquillace(RepairCenter rc){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Constants.SQUILLACE_REPORT_PATH))) {
            writer.write(rc.toString());
        } catch (IOException e) {
            log.error("Failed to write to Squillace Report: {}", e.getMessage());
        }
    }

    private void copyAndRenameFile(String templatePdfPath, String outputPdfPath) {
        File templateFile = new File(templatePdfPath);
        if (!templateFile.exists()) {
            log.error("Template file not found: {}", templatePdfPath);
            return;
        }
        File outputFile = new File(outputPdfPath);
        File parent = outputFile.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            log.error("Could not create output directory: {}", parent);
            return;
        }
        try {
            if (!outputFile.exists()) {
                Files.copy(templateFile.toPath(),
                        outputFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
                log.debug("Copied template PDF to: {}", outputPdfPath);
            }
        } catch (IOException e) {
            log.error("Error while copying PDF from {} to {}", templatePdfPath, outputPdfPath, e);
        }
    }

    private void writeText(PDPageContentStream contentStream, String text, int x, int y) throws IOException {
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 11);
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();
    }

}
