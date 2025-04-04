package org.example.diplomabackend.mailsender;

import lombok.RequiredArgsConstructor;
import org.example.diplomabackend.auth.entities.AuthService;
import org.example.diplomabackend.ticket.entities.TicketEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:credentials.properties")
public class MailSenderService {

    private final JavaMailSender javaMailSender;
    private final AuthService authService;

    @Value("${mail.username}")
    private String username;


    public void sendTicketCreationMessage(TicketEntity t) {
        String email = authService.getEmailById(t.getSubmitter_id()).getBody();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(username);
        message.setTo(email);
        message.setSubject("Ваше звернення було зареєстроване.");
        message.setText(String.format(
                "Шановний користувач! Нами було прийняте та зареєстроване звернення від %s з темою \"%s\".\nБудь ласка, очікуйте відповіді від адміністратора."
                ,t.getSubmit_date(),t.getTopic()));
        javaMailSender.send(message);

    }
    public void sendTicketRejectionMessage(TicketEntity t) {
        String email = authService.getEmailById(t.getSubmitter_id()).getBody();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(username);
        message.setTo(email);
        message.setSubject("Ваше звернення було закрите.");
        message.setText(String.format(
                "Шановний користувач! Ваше звернення від %s з темою \"%s\" було опрацьоване адміністратором.\nВоно було відхилене з причиною:\n%s"
                ,t.getSubmit_date(),t.getTopic(), t.getReport_feedback()));
        javaMailSender.send(message);

    }

    public void sendTicketClosingMessage(TicketEntity t) {
        String email = authService.getEmailById(t.getSubmitter_id()).getBody();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(username);
        message.setTo(email);
        message.setSubject("Ваше звернення було закрите.");
        message.setText(String.format(
                "Шановний користувач! Ваше звернення від %s з темою \"%s\" було опрацьоване адміністратором.\nВідповідь адміністратора:\n%s"
                ,t.getSubmit_date(), t.getTopic(), t.getReport_feedback()));
        javaMailSender.send(message);

    }
}
