package com.api.authapi.application.helpers;

import com.api.authapi.config.properties.AppProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final AppProperties appProperties;

    public void sendReactivationEmail(String to, UUID token) {
        String link = appProperties.getBaseUrl() + "/me/enable/" + token;

        String subject = "Reactivá tu cuenta";
        String content = """
                <p>Hola,</p>
                <p>Recibimos tu solicitud para reactivar tu cuenta. Hacé clic en el siguiente enlace:</p>
                <a href="%s">Reactivar cuenta</a>
                <p>Este enlace caduca en 24 horas.</p>
                """.formatted(link);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("No se pudo enviar el mail de reactivación", e);
        }
    }

    public void sendActivationEmail(String to, UUID token, UUID sagaId) {
        String link = appProperties.getBaseUrl() + "/auth/activation/" + token + "/" + sagaId;

        String subject = "Activa tu cuenta";
        String content = """
                <p>Hola,</p>
                <p>Recibimos tu solicitud para activar tu cuenta. Hacé clic en el siguiente enlace:</p>
                <a href="%s">Activa cuenta</a>
                <p>Este enlace caduca en 24 horas.</p>
                """.formatted(link);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("No se pudo enviar el mail de activación", e);
        }
    }
}