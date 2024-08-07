package io.github.mroncatto.itflow.domain.email.model;

import io.github.mroncatto.itflow.domain.email.entity.EmailEventData;
import io.github.mroncatto.itflow.domain.email.entity.EmailSendRecipient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.github.mroncatto.itflow.application.config.constant.ApplicationConstant.APP_NAME;

@Slf4j
public abstract class AbstractEmailService {

    private String getDomain() {
        return "itflow.com"; //TODO: Criar metodo para registrar dominio
    }

    protected MimeMessage buildMimeMessage(MimeMessage mimeMessage, String subject, String body, String... recipients){
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setSubject(APP_NAME.concat(" - ").concat(subject));
            helper.setFrom("noreply@".concat(getDomain()));
            helper.setTo(recipients);
            helper.setText(body, true);
        } catch (MessagingException e){
            log.error("Error while building Mime Message: {}", e.getMessage());
        }
        return mimeMessage;
    }

    protected List<EmailSendRecipient> getRecipients(String... email) {
        List<EmailSendRecipient> recipients = new ArrayList<>();
        Arrays.stream(email).forEach(e -> {
            recipients.add(
                    EmailSendRecipient.builder()
                            .recipient(e)
                            .build());
        });
        return recipients;
    }

    protected List<EmailEventData> getVariables(List<String> variables, String... values) throws Exception {
        if (variables.size() != values.length) throw new Exception("ERROR COMPOSING EMAIL: The parameters and variables do not match");
        List<EmailEventData> eventDataList = new ArrayList<>();
        variables.forEach(v -> {
            eventDataList.add(
                    EmailEventData.builder()
                            .variableName(v)
                            .variableValue(values[variables.indexOf(v)])
                            .build());
        });
        return eventDataList;
    }
}
