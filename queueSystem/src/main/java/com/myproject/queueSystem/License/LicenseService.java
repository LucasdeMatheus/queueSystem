package com.myproject.queueSystem.License;

import com.myproject.queueSystem.domain.user.User;
import com.myproject.queueSystem.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LicenseService {

    @Autowired
    public LicenseRepository licenseRepository;

    @Autowired
    public BCryptPasswordEncoder encoder;

    @Autowired
    public UserRepository userRepository;

    public ResponseEntity<?> activateLicense(String code) {
        User user = userRepository.findTopByOrderByIdAsc()
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        List<License> allLicenses = licenseRepository.findAll();

        for (License license : allLicenses) {
            if (!license.isUsed() && encoder.matches(code, license.getCode())) {
                license.setUsed(true);
                license.setUsedAt(LocalDateTime.now());
                licenseRepository.save(license);
                if (user.getValidUntil() != null) {
                    user.setValidUntil(user.getValidUntil().plusDays(30));
                } else {
                    user.setValidUntil(LocalDateTime.now().plusDays(30));
                }
                userRepository.save(user);
                return ResponseEntity.ok(user.getValidUntil());
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Licença usada ou não existe.");
    }



    public boolean checkLicense() {
        User user = userRepository.findTopByOrderByIdAsc()
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (user.getValidUntil() == null || user.getValidUntil().isBefore(LocalDateTime.now())) {
            return false;
        }

        return true;
    }




}
