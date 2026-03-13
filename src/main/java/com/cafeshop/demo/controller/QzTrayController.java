package com.cafeshop.demo.controller;

import com.cafeshop.demo.service.QzTraySigningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qz")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class QzTrayController {

    private final QzTraySigningService qzTraySigningService;

    @GetMapping(value = "/certificate", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getCertificate() {
        return qzTraySigningService.getCertificate();
    }

    @PostMapping(value = "/sign", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public String sign(@RequestBody String request) {
        return qzTraySigningService.sign(request);
    }
}