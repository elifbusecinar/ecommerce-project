package com.ecommerce.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')") //yeni ekledim
    public ResponseEntity<String> userAccess() {
        return ResponseEntity.ok("Only logged-in users can see this!");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminAccess() {
        return ResponseEntity.ok("Only ADMINs can see this!");
    }
}

