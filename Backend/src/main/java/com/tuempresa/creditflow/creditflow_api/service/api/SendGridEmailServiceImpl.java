package com.tuempresa.creditflow.creditflow_api.service.api;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.tuempresa.creditflow.creditflow_api.service.IEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class SendGridEmailServiceImpl implements IEmailService {

    // Spring now injects the SendGrid bean automatically
    private final SendGrid sendGrid;

    @Value("${app.sendgrid.from-email}")
    private String fromEmail;

    @Override
    public void sendEmail(String to, String subject, String body) {
        Email from = new Email(fromEmail);
        Email toRecipient = new Email(to);
        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, toRecipient, content);

        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);
            log.info("Email sent to {}. Status code: {}", to, response.getStatusCode());
            log.debug("Response body: {}", response.getBody());
        } catch (IOException ex) {
            log.error("Error sending email to {}", to, ex);
            // Consider throwing a custom exception here to handle it in a higher layer
        }
    }
}