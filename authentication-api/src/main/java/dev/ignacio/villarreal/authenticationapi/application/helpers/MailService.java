package dev.ignacio.villarreal.authenticationapi.application.helpers;

import dev.ignacio.villarreal.authenticationapi.config.properties.AppProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final AppProperties appProperties;

    public void sendAccountActivation(String to, UUID token, UUID sagaId) {
        String link = appProperties.getBaseUrl() + "/account/verify/" + token + "/" + sagaId;

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

    public void sendAccountReactivation(String to, UUID token) {
        String link = appProperties.getBaseUrl() + "/user/account/reactivate" + token;

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
}