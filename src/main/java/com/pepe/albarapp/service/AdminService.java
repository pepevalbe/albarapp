package com.pepe.albarapp.service;

import com.pepe.albarapp.persistence.domain.Invitation;
import com.pepe.albarapp.persistence.domain.User;
import com.pepe.albarapp.persistence.repository.InvitationRepository;
import com.pepe.albarapp.persistence.repository.UserRepository;
import com.pepe.albarapp.service.dto.RegistrationDto;
import com.pepe.albarapp.service.dto.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class AdminService {

    private static final long EXPIRATION_TIME_MILLIS = 24 * 60 * 60 * 1000;     // 24h expiration time

    @Value("${albarapp.create_user_url}")
    private String createUserUrl;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    public void sendInvitation(String email, String role) {

        if (!EmailService.isValidEmail(email)) {
            throw new RuntimeException("Invalid email!");
        }

        if (!UserRole.contains(role)) {
            throw new RuntimeException("Invalid role!");
        }

        Invitation invitation = invitationRepository.save(new Invitation(email, role));
        String link = createUserUrl.concat(invitation.getToken());
        emailService.sendInvitation(email, link);
    }

    @Transactional
    public User createUser(RegistrationDto registrationDto) {

        Invitation invitation = invitationRepository.findByToken(registrationDto.getToken())
                .orElseThrow(() -> new RuntimeException("Token not found!"));

        if (System.currentTimeMillis() > invitation.getIssuedTimestamp() + EXPIRATION_TIME_MILLIS) {
            throw new RuntimeException("Invitation expired!");
        }

        // TODO: Hash password
        User user = new User(invitation.getEmail(),
                registrationDto.getPassword(),
                registrationDto.getName(),
                registrationDto.getSurname(),
                invitation.getRole());

        User createdUser = userRepository.save(user);
        emailService.sendWelcomeEmail(createdUser);

        return createdUser;
    }
}