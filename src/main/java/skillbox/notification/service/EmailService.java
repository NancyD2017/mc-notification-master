package skillbox.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import skillbox.notification.model.MailBody;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender javaMailSender;

    public void sendSimpleMessage(MailBody mailBody) {
        if (mailBody == null || mailBody.getTo() == null || mailBody.getSubject() == null || mailBody.getText() == null) {
            log.error("Invalid mail body: {}", mailBody);
            return;
        }

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(mailBody.getTo());
        mailMessage.setSubject(mailBody.getSubject());
        mailMessage.setText(mailBody.getText());
        mailMessage.setFrom("anastasia.alifanowa2017@yandex.ru");

        try {
            javaMailSender.send(mailMessage);
            log.info("Email sent successfully to {}", mailBody.getTo());
        } catch (MailException e) {
            log.error("Failed to send email to {}: {}", mailBody.getTo(), e.getMessage(), e);
        }
    }
}