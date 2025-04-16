package it.chrccl.carrozzeriaonline.components;

import it.chrccl.carrozzeriaonline.bot.BotContext;
import it.chrccl.carrozzeriaonline.model.Constants;
import it.chrccl.carrozzeriaonline.model.dao.Partner;
import it.chrccl.carrozzeriaonline.model.dao.TaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.stream.Stream;

@Slf4j
@Component
public class IOComponent {

    public void writeOnWarrantFile(BotContext context, Partner partner, String userPhone, String text, int x, int y){
        String templatePdfPath = partner == Partner.CARLINK
                ? Constants.CARLINK_WARRANT_PATH
                : Constants.SAVOIA_WARRANT_PATH;

        String outputPdfPath = partner == Partner.CARLINK
                ? String.format(Constants.USER_CARLINK_WARRANT_PATH_FORMAT, userPhone)
                : String.format(Constants.USER_SAVOIA_WARRANT_PATH_FORMAT, userPhone);

        if (context.getTask().getStatus() == TaskStatus.DATE) {
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

    public void signWarrantFile(Partner partner, String userPhone, String text, int x, int y) {
        try {
            String outputPdfPath = partner == Partner.CARLINK
                    ? String.format(Constants.USER_CARLINK_WARRANT_PATH_FORMAT, userPhone)
                    : String.format(Constants.USER_SAVOIA_WARRANT_PATH_FORMAT, userPhone);

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
        Path warrantPath = Path.of(String.format(Constants.USER_CARLINK_WARRANT_PATH_FORMAT, fromNumber));
        if (!Files.exists(warrantPath)) {
            log.debug("The directory does not exist: {}", warrantPath);
            return;
        }
        // Walk the directory tree in reverse order (files first, then directories)
        try (Stream<Path> paths = Files.walk(warrantPath)) {
            paths.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            log.error("Failed to delete: {} ({})", path, e.getMessage());
                        }
                    });
        } catch (IOException e) {
            log.error("Error while deleting tmp logs: {}", e.getMessage());
        }
    }

    private void copyAndRenameFile(String templatePdfPath, String outputPdfPath) {
        try {
            File templateFile = new File(templatePdfPath);
            File outputFile = new File(outputPdfPath);

            // Only copy if the file does not already exist (to avoid unnecessary overwrites)
            if (!outputFile.exists()) {
                Files.copy(templateFile.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                log.debug("Copied template PDF to: {}", outputPdfPath);
            }
        } catch (IOException e) {
            log.error("Error while copying the PDF: {}", String.valueOf(e.getCause()));
            return; // Stop execution if copying fails
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
