package org.commontech.gardentown.infrastructure.adapter.outgoing.email;

public interface EmailService {
    void sendSimpleMessage(
            String to, String subject, String text);
}
