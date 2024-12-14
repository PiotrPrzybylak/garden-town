package org.commontech.gardentown.infrastructure.adapter.outgoing.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
record JavaEmailService(JavaMailSender emailSender, @Value("${email.sender}") String emailFrom, @Value("${email.recipient}") String emailTo ) implements EmailService {

    @Override
    public void sendSimpleMessage(
            String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailFrom);
        message.setTo(emailTo);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }
}