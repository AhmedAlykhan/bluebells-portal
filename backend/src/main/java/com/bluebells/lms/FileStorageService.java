package com.bluebells.lms;

import com.bluebells.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

    private final Path storageLocation;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx", "txt",
            "jpg", "jpeg", "png", "webp", "gif"
    );

    private static final Set<String> BLOCKED_EXTENSIONS = Set.of(
            "exe", "sh", "bat", "cmd", "jsp", "php", "py", "pl", "cgi",
            "html", "htm", "js", "vbs", "jar", "war", "ear"
    );

    public FileStorageService(@Value("${bluebells.storage.upload-dir:./storage/uploads}") String uploadDir) {
        this.storageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(this.storageLocation);
            Files.createDirectories(this.storageLocation.resolve("submissions"));
            Files.createDirectories(this.storageLocation.resolve("resources"));
            log.info("LMS File storage directory initialized at: {}", this.storageLocation);
        } catch (IOException e) {
            log.error("Could not initialize file storage directory: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not initialize file upload storage directory");
        }
    }

    public StoredFile storeFile(MultipartFile file, String subfolder) {
        if (file.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot upload empty file");
        }

        String rawFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "file");
        if (rawFilename.contains("..")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Filename contains invalid path sequence: " + rawFilename);
        }

        String extension = getFileExtension(rawFilename).toLowerCase();
        if (BLOCKED_EXTENSIONS.contains(extension)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "File type ." + extension + " is prohibited for security reasons");
        }

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "File type ." + extension + " is not supported. Allowed formats: PDF, DOCX, PPTX, Images.");
        }

        validateMagicBytes(file, extension);

        String uniqueFilename = UUID.randomUUID().toString() + "_" + rawFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
        Path targetDir = subfolder != null && !subfolder.isBlank()
                ? this.storageLocation.resolve(subfolder)
                : this.storageLocation;

        try {
            Files.createDirectories(targetDir);
            Path targetPath = targetDir.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            String relativePath = (subfolder != null && !subfolder.isBlank() ? subfolder + "/" : "") + uniqueFilename;
            return new StoredFile(
                    relativePath,
                    rawFilename,
                    file.getSize(),
                    file.getContentType() != null ? file.getContentType() : "application/octet-stream",
                    determineFileType(extension)
            );
        } catch (IOException e) {
            log.error("Failed to store file: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not store file on server");
        }
    }

    public Resource loadFileAsResource(String relativePath) {
        try {
            Path filePath = this.storageLocation.resolve(relativePath).normalize();
            if (!filePath.startsWith(this.storageLocation)) {
                throw new ApiException(HttpStatus.FORBIDDEN, "Access denied: Path traversal attempt detected");
            }

            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ApiException(HttpStatus.NOT_FOUND, "File not found or cannot be read: " + relativePath);
            }
        } catch (MalformedURLException e) {
            throw new ApiException(HttpStatus.NOT_FOUND, "File not found: " + relativePath);
        }
    }

    private void validateMagicBytes(MultipartFile file, String extension) {
        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[8];
            int bytesRead = is.read(header);
            if (bytesRead < 4) {
                return; // Small file, allow if passes extension check
            }

            if ("pdf".equals(extension)) {
                // %PDF
                if (header[0] != 0x25 || header[1] != 0x50 || header[2] != 0x44 || header[3] != 0x46) {
                    throw new ApiException(HttpStatus.BAD_REQUEST, "File content does not match valid PDF header signature");
                }
            } else if ("png".equals(extension)) {
                // 0x89 P N G
                if ((header[0] & 0xFF) != 0x89 || header[1] != 0x50 || header[2] != 0x4E || header[3] != 0x47) {
                    throw new ApiException(HttpStatus.BAD_REQUEST, "File content does not match valid PNG image signature");
                }
            } else if ("jpg".equals(extension) || "jpeg".equals(extension)) {
                // 0xFF 0xD8 0xFF
                if ((header[0] & 0xFF) != 0xFF || (header[1] & 0xFF) != 0xD8 || (header[2] & 0xFF) != 0xFF) {
                    throw new ApiException(HttpStatus.BAD_REQUEST, "File content does not match valid JPEG image signature");
                }
            }
        } catch (IOException e) {
            log.warn("Could not inspect magic bytes for file validation: {}", e.getMessage());
        }
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);
    }

    private String determineFileType(String extension) {
        if (Arrays.asList("jpg", "jpeg", "png", "webp", "gif").contains(extension)) {
            return "IMAGE";
        } else if ("pdf".equals(extension)) {
            return "PDF";
        } else if (Arrays.asList("doc", "docx").contains(extension)) {
            return "DOCX";
        } else if (Arrays.asList("ppt", "pptx").contains(extension)) {
            return "PPTX";
        } else if (Arrays.asList("xls", "xlsx").contains(extension)) {
            return "EXCEL";
        }
        return "OTHER";
    }

    public static class StoredFile {
        private final String relativePath;
        private final String originalFilename;
        private final long size;
        private final String contentType;
        private final String fileType;

        public StoredFile(String relativePath, String originalFilename, long size, String contentType, String fileType) {
            this.relativePath = relativePath;
            this.originalFilename = originalFilename;
            this.size = size;
            this.contentType = contentType;
            this.fileType = fileType;
        }

        public String getRelativePath() {
            return relativePath;
        }

        public String getOriginalFilename() {
            return originalFilename;
        }

        public long getSize() {
            return size;
        }

        public String getContentType() {
            return contentType;
        }

        public String getFileType() {
            return fileType;
        }
    }
}
