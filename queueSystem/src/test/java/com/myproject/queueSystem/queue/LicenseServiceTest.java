package com.myproject.queueSystem.queue;

import com.myproject.queueSystem.License.License;
import com.myproject.queueSystem.License.LicenseRepository;
import com.myproject.queueSystem.License.LicenseService;
import com.myproject.queueSystem.domain.user.User;
import com.myproject.queueSystem.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LicenseServiceTest {

    private LicenseService licenseService;
    private LicenseRepository licenseRepository;
    private UserRepository userRepository;
    private BCryptPasswordEncoder encoder;

    @BeforeEach
    void setup() {
        licenseRepository = mock(LicenseRepository.class);
        userRepository = mock(UserRepository.class);
        encoder = mock(BCryptPasswordEncoder.class);

        licenseService = new LicenseService();
        // injetar mocks via reflection ou setter (se não tiver construtor)
        // ou usar @InjectMocks + @Mock no Mockito (se usar MockitoExtension)
        licenseService.licenseRepository = licenseRepository;
        licenseService.userRepository = userRepository;
        licenseService.encoder = encoder;
    }

    @Test
    void activateLicense_success() {
        User user = new User();
        user.setValidUntil(LocalDateTime.now().plusDays(5));

        License license = new License();
        license.setUsed(false);
        license.setCode("encodedCode");

        when(userRepository.findTopByOrderByIdAsc()).thenReturn(Optional.of(user));
        when(licenseRepository.findAll()).thenReturn(List.of(license));
        when(encoder.matches("rawCode", "encodedCode")).thenReturn(true);

        ResponseEntity<?> response = licenseService.activateLicense("rawCode");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        // verificar se license foi marcada como usada
        assertTrue(license.isUsed());

        // verificar se usuário teve validade atualizada
        assertTrue(user.getValidUntil().isAfter(LocalDateTime.now()));

        verify(licenseRepository).save(license);
        verify(userRepository).save(user);
    }

    @Test
    void activateLicense_invalidCode() {
        User user = new User();
        when(userRepository.findTopByOrderByIdAsc()).thenReturn(Optional.of(user));
        when(licenseRepository.findAll()).thenReturn(List.of());

        ResponseEntity<?> response = licenseService.activateLicense("wrongCode");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Licença usada ou não existe.", response.getBody());
    }

    @Test
    void checkLicense_expired() {
        User user = new User();
        user.setValidUntil(LocalDateTime.now().minusDays(1));
        when(userRepository.findTopByOrderByIdAsc()).thenReturn(Optional.of(user));

        boolean result = licenseService.checkLicense();

        assertFalse(result);
    }

    @Test
    void checkLicense_valid() {
        User user = new User();
        user.setValidUntil(LocalDateTime.now().plusDays(1));
        when(userRepository.findTopByOrderByIdAsc()).thenReturn(Optional.of(user));

        boolean result = licenseService.checkLicense();

        assertTrue(result);
    }
}
