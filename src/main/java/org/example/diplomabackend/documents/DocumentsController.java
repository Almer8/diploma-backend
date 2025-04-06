package org.example.diplomabackend.documents;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/documents")
public class DocumentsController {

    private final DocumentsService documentsService;

    @GetMapping
    public ResponseEntity<?> checkDocuments() {
        return documentsService.checkDocuments();
    }

    @PostMapping
    public ResponseEntity<?> sendDocuments(
            @RequestPart(value = "id") List<MultipartFile> idFiles,
            @RequestPart(value = "certificates") List<MultipartFile> certificates) {
        return documentsService.sendDocuments(idFiles, certificates);
    }
}
