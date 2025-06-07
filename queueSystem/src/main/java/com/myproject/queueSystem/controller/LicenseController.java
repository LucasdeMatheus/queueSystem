package com.myproject.queueSystem.controller;

import com.myproject.queueSystem.License.LicenseService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/license")
@SecurityRequirement(name = "bearer-key")
public class LicenseController {

    @Autowired
    LicenseService licenseService;

    @PostMapping("/{code}")
    public ResponseEntity<?> generatedQueue(@PathVariable String code) {
        return licenseService.activateLicense(code);

    }
}
