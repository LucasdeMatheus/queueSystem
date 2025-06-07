package com.myproject.queueSystem.controller;

import com.myproject.queueSystem.License.LicenseService;
import com.myproject.queueSystem.domain.user.DataLogin;
import com.myproject.queueSystem.domain.user.User;
import com.myproject.queueSystem.infra.DataTokenJWT;
import com.myproject.queueSystem.infra.TokenService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AutenticacaoController {
    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private LicenseService licenseService;

    @PostMapping
    public ResponseEntity toLogin(@RequestBody @Valid DataLogin dados) {
        if (!licenseService.checkLicense()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Licença expirada.");
        }
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.login(), dados.senha());
        var authentication = manager.authenticate(authenticationToken);

        var tokenJWT = tokenService.gerarToken((User) authentication.getPrincipal());

        return ResponseEntity.ok(new DataTokenJWT(tokenJWT));

    }

}
