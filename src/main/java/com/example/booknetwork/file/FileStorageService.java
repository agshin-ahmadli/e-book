package com.example.booknetwork.file;

import com.example.booknetwork.book.Book;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {

   // @Value("${application.file.upload.photos-output-path}")
    private String fileUploadPath;
    public String saveFile(
          @NonNull MultipartFile sourceFile,
          @NonNull Integer userId) {
        final String fileUploadSubPath = "users" + File.separator + userId;
        return upload(sourceFile,fileUploadSubPath);
    }

    private String upload(
            @NonNull MultipartFile sourceFile,
            @NonNull String fileUploadSubPath) {
        final String finalUploadPath = fileUploadPath + File.separator + fileUploadSubPath;
        File targetFolder = new File(fileUploadPath);
        if(!targetFolder.exists()){
            boolean folderCreated = targetFolder.mkdirs();
            if(!folderCreated){
                log.warn("Failed to create the target folder");
                return null;
            }
        }
        final String fileExtension = getFileExtension(sourceFile.getOriginalFilename());
        String targetFile = finalUploadPath + File.separator + System.currentTimeMillis() + "." +fileExtension;
        Path targetPath = Paths.get(targetFile);
        try{
            Files.write(targetPath,sourceFile.getBytes());
            log.info("File saved to " + targetFile);
            return targetFile;
        }catch (IOException e){
            log.error("File was not saved", e);
        }
        return null;
    }

    private String getFileExtension(String originalFilename) {
        if(originalFilename == null || originalFilename.isEmpty()){
            return null;
        }
        int lastDotIndex = originalFilename.lastIndexOf(".");

        if(lastDotIndex == -1){
            return "";
        }
        return originalFilename.substring(lastDotIndex + 1).toLowerCase();
    }
}
