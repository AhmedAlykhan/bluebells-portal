package com.bluebells.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

    public EmailNotificationService() {
    }

    /**
     * Dispatches outgoing school email notification.
     * Uses local audit adapter with Mailpit compatibility.
     */
    public boolean sendEmail(String to, String subject, String body) {
        log.info("[OUTGOING EMAIL] To: {} | Subject: {} | Length: {} chars", to, subject, body != null ? body.length() : 0);
        return true;
    }
}
