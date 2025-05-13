package it.chrccl.carrozzeriaonline.services;

import it.chrccl.carrozzeriaonline.components.TwilioComponent;
import it.chrccl.carrozzeriaonline.model.bot.BotContext;
import it.chrccl.carrozzeriaonline.model.bot.MessageData;
import it.chrccl.carrozzeriaonline.model.entities.Attachment;
import it.chrccl.carrozzeriaonline.model.entities.Task;
import it.chrccl.carrozzeriaonline.repos.AttachmentRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class AttachmentService {

    @Value("${attachments.base-dir}")
    private String baseDir;

    private final TwilioComponent twilio;

    private final AttachmentRepo repo;

    @Autowired
    public AttachmentService(AttachmentRepo repo, TwilioComponent twilio) {
        this.repo = repo;
        this.twilio = twilio;
    }

    public List<Attachment> findAttachmentsByTask(Task task) {
        return repo.findAttachmentsByTask(task);
    }

    public Attachment save(Attachment attachment) {
        return repo.save(attachment);
    }

    public void saveAll(List<Attachment> attachments) {
        repo.saveAll(attachments);
    }

    public Attachment createFromUrl(BotContext context, MessageData data) {
        try {
            byte[] bytes = downloadBytes(data.getMediaUrlAttachment(), twilio.getUserCredentials());
            return storeAttachment(bytes,
                    data.getContentTypeAttachment(),
                    extractOriginalName(data.getMediaUrlAttachment()),
                    data.getMediaUrlAttachment(),
                    context.getTask(),
                    true);
        } catch (IOException e) {
            return null;
        }
    }

    public Attachment createFromBase64Attachment(Task task, String base64Data, String originalName,
            String url, String contentType) {
        try {
            byte[] bytes = Base64.getDecoder().decode(base64Data);
            log.debug("Decodificati {} byte da Base64 per {}", bytes.length, originalName);
            return storeAttachment(
                    bytes,
                    contentType,
                    originalName,
                    url,
                    task,
                    false
            );
        } catch (IllegalArgumentException | IOException e) {
            log.error("Impossibile processare attachment Base64 {}: {}", originalName, e.getMessage(), e);
            return null;
        }
    }

    public byte[] getFileBytes(String filePath) throws IOException {
        log.info("Caricamento attachment da filePath: {}", filePath);
        Path path = Paths.get(filePath);
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            log.error("File non trovato o non valido: {}", filePath);
            return null;
        }
        return Files.readAllBytes(path);
    }



    private byte[] downloadBytes(String urlString, String basicAuthCredentials) throws IOException {
        log.debug("Scarico attachment da URL {} con credenziali Basic", urlString);
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        conn.setRequestProperty("Authorization",
                "Basic " + Base64.getEncoder().encodeToString(basicAuthCredentials.getBytes(StandardCharsets.UTF_8)));
        try (InputStream in = conn.getInputStream()) {
            return in.readAllBytes();
        }
    }

    private Attachment storeAttachment(byte[] data,
                                       String contentType,
                                       String originalName,
                                       String sourceUrl,
                                       Task task,
                                       boolean persistenceActive) throws IOException {
        // 1) Crea la directory per questo task
        String taskDirName = task.getUser().getMobilePhone() + "_" +
                task.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        Path dir = Paths.get(baseDir, taskDirName);
        Files.createDirectories(dir);

        // 2) Genera un nome file univoco
        String filename = UUID.randomUUID() + "_" + originalName;
        Path filePath = dir.resolve(filename);
        Files.write(filePath, data, StandardOpenOption.CREATE);

        log.info("Attachment salvato su file system: {}", filePath);

        // 3) Costruisci e salva l’entità
        Attachment attachment = Attachment.builder()
                .name(originalName)
                .contentType(contentType)
                .filePath(filePath.toString())
                .url(sourceUrl)
                .task(task)
                .build();

        if (persistenceActive) {
            return repo.save(attachment);
        }else{
            return attachment;
        }
    }

    private String extractOriginalName(String url) {
        try {
            return Paths.get(new URI(url).getPath()).getFileName().toString();
        } catch (Exception e) {
            log.warn("Impossibile estrarre nome da URL {}, uso nome generato", url, e);
            return "attachment_" + UUID.randomUUID();
        }
    }

}
