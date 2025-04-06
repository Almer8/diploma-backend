package org.example.diplomabackend.documents;


import org.example.diplomabackend.auth.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

@Service
public class DocumentsService {

    @Value("${filestorage.directory}")
    private String UPLOAD_DIRECTORY;

    @PreAuthorize("hasAuthority('DOCTOR')")
    public ResponseEntity<?> checkDocuments(){

        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Path idUploadPath = Paths.get(UPLOAD_DIRECTORY.concat(String.format("/documents/user_%s/id",user.getId())));
        Path certificatesUploadPath = Paths.get(UPLOAD_DIRECTORY.concat(String.format("/documents/user_%s/certificates",user.getId())));
        try (Stream<Path> entries = Files.list(idUploadPath)) {
            if (entries.findAny().isEmpty()) {
                return ResponseEntity.notFound().build();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (Stream<Path> entries = Files.list(certificatesUploadPath)){
            if (entries.findAny().isEmpty()) {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok().build();

    }


    @PreAuthorize("hasAuthority('DOCTOR')")
    public ResponseEntity<?> sendDocuments(List<MultipartFile> idFiles, List<MultipartFile> certificates){
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer id_counter = 0;
        Integer certificates_counter = 0;
        Path idUploadPath = Paths.get(UPLOAD_DIRECTORY.concat(String.format("/documents/user_%s/id",user.getId())));
        Path certificatesUploadPath = Paths.get(UPLOAD_DIRECTORY.concat(String.format("/documents/user_%s/certificates",user.getId())));
        Path filePath;
        try {


            if (!Files.exists(idUploadPath)) {
                Files.createDirectories(idUploadPath);
            }
            if (!Files.exists(certificatesUploadPath)) {
                Files.createDirectories(certificatesUploadPath);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for(MultipartFile file : idFiles){
            String filename = file.getOriginalFilename();
            String extention = "";
            if(filename != null && filename.contains(".")){
                extention = filename.substring(filename.lastIndexOf("."));
            }
            String uploadFilename = String.format("user%s_id_%s%s",user.getId(),++id_counter,extention);

            try {
                filePath = idUploadPath.resolve(uploadFilename);
                file.transferTo(filePath.toFile());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        for(MultipartFile file : certificates){
            String filename = file.getOriginalFilename();
            String extention = "";
            if(filename != null && filename.contains(".")){
                extention = filename.substring(filename.lastIndexOf("."));
            }
            String uploadFilename = String.format("user%s_certificate_%s%s",user.getId(),++certificates_counter,extention);

            try {
                filePath = certificatesUploadPath.resolve(uploadFilename);
                file.transferTo(filePath.toFile());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return ResponseEntity.ok("Files saved");
    }
}
