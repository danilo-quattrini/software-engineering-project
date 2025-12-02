package it.unicam.cs.ids2425.product.certification.controller;

import it.unicam.cs.ids2425.product.certification.Certificate;
import it.unicam.cs.ids2425.product.certification.service.CertificateService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/certificate")
public class CertificateController {


    private final CertificateService certificateService;

    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadCertificate(@RequestParam("file") MultipartFile file)
            throws IOException {

        Map<String, String> map = new HashMap<>();

        // Populate the map with file details
        map.put("fileName", file.getOriginalFilename());
        map.put("fileSize", String.valueOf(file.getSize()));
        map.put("fileContentType", file.getContentType());

        // File upload is successful
        certificateService.store(file);
        map.put("message", "File upload done");
        return ResponseEntity.ok(map);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadCertificate(@PathVariable("id") Long id) {
        Certificate certificate = certificateService.getFile(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + certificate.getName() + "\"")
                .body(certificate.getData());
    }

    @DeleteMapping("/{id}")
    public String removeCertificate(@PathVariable("id") Long id) {
        certificateService.removeFile(id);
        return "redirect:/products";
    }

}
