package com.myproject.queueSystem.infra;

import com.myproject.queueSystem.domain.user.User;
import com.myproject.queueSystem.domain.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppInitializer {

    @Bean
    public CommandLineRunner initAdmin(UserRepository repo, PasswordEncoder encoder) {
        return args -> {
            if (repo.count() == 0) {
                User user = new User();
                user.setLogin("admin");
                user.setPassword(encoder.encode("admin123"));
                repo.save(user);
                System.out.println("Usuário admin criado.");
            }
        };
    }
}

