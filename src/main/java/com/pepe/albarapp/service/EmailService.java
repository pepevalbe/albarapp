package com.pepe.albarapp.service;

import com.pepe.albarapp.persistence.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

@Slf4j
@Component
public class EmailService {

    private static final String INVITATION_SUBECT = "Invitación para albarapp";
    private static final String INVITATION_TEMPLATE = "Has sido invitado a albarapp, utilice el siguiente link para registrarse: %s";
    private static final String WELCOME_EMAIL = "Bienvenido a albarapp";
    private static final String WELCOME_TEMPLATE = "Hola %s, ya puedes entrar a tu cuenta con tu email y contraseña";

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Autowired
    private JavaMailSender javaMailSender;

    public static boolean isValidEmail(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    @Async
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
        log.info("Sent email to: " + to);
    }

    @Async
    public void sendInvitation(String email, String link) {
        String text = String.format(INVITATION_TEMPLATE, link);
        log.info("Sending invitation to: " + email);
        sendEmail(email, INVITATION_SUBECT, text);
    }

    @Async
    public void sendWelcomeEmail(User user) {
        String text = String.format(WELCOME_TEMPLATE, user.getName());
        log.info("Sending welcome email to: " + user.getName() + " - " + user.getEmail());
        sendEmail(user.getEmail(), WELCOME_EMAIL, text);
    }
}
