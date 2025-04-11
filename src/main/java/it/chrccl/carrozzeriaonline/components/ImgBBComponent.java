package it.chrccl.carrozzeriaonline.components;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ImgBBComponent {

    @Value("${imgbb.api.key}")
    private String API_KEY;

    public String uploadImage(String imagePath, String fileName) throws IOException {
        File sourceFile = validateSourceFile(imagePath);
        log.info("Converting PDF {} to image...", imagePath);
        File imageFile = convertPDFToImage(sourceFile, fileName);
        String base64Image = encodeImageToBase64(imageFile);
        HttpURLConnection connection = createConnection(base64Image);
        int responseCode = connection.getResponseCode();
        log.debug("Received HTTP response code: {}", responseCode);
        return processResponse(connection, responseCode);
    }

    private File validateSourceFile(String imagePath) {
        File pdfFile = new File(imagePath);
        if (!pdfFile.exists() || !pdfFile.isFile()) {
            throw new IllegalArgumentException("Invalid image file path: " + imagePath);
        }
        return pdfFile;
    }

    private File convertPDFToImage(File pdfFile, String fileName) throws IOException {
        String imagePath = convertPDFToImages(pdfFile.getAbsolutePath(), fileName);
        return new File(imagePath);
    }

    private HttpURLConnection createConnection(String base64Image) throws IOException {
        String urlString = "https://api.imgbb.com/1/upload?key=" + API_KEY;
        log.debug("Uploading image to ImgBB endpoint: {}", urlString);
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);
        String postData = "image=" + URLEncoder.encode(base64Image, StandardCharsets.UTF_8);
        try (OutputStream os = connection.getOutputStream()) {
            os.write(postData.getBytes(StandardCharsets.UTF_8));
            os.flush();
        }
        return connection;
    }

    private String processResponse(HttpURLConnection connection, int responseCode) throws IOException {
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String response = readStream(connection.getInputStream());
            log.debug("ImgBB API response: {}", response);
            JSONObject jsonResponse = new JSONObject(response);
            if (jsonResponse.has("data")) {
                String imageUrl = jsonResponse.getJSONObject("data").getString("url");
                log.info("Image uploaded successfully. URL: {}", imageUrl);
                return imageUrl;
            } else {
                throw new IOException("Unexpected API response: " + response);
            }
        } else {
            String errorResponse = readStream(connection.getErrorStream());
            log.error("ImgBB API error response (HTTP {}): {}", responseCode, errorResponse);
            throw new IOException("HTTP error code: " + responseCode + ". Error response: " + errorResponse);
        }
    }

    private String readStream(InputStream stream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining());
        } catch (IOException e) {
            log.error("Error reading stream", e);
            return "";
        }
    }

    private String encodeImageToBase64(File imageFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(imageFile);
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            byte[] imageBytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        }
    }

    private String convertPDFToImages(String pdfFilePath, String fileName) throws IOException {
        File pdfFile = new File(pdfFilePath);
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            BufferedImage image = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);
            File outputFile = new File("/opt/tomcat/", fileName + ".png");
            ImageIO.write(image, "PNG", outputFile);
            System.out.println("Saved: " + outputFile.getAbsolutePath());
            return "/opt/tomcat/" + fileName + ".png";
        }
    }
}
