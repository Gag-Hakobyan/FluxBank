package com.fluxbank.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
//    private final MainService mainService;

    @Async
    public void sendVerificationMail(String to, String subject, String message){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);
        javaMailSender.send(simpleMailMessage);
    }

    // With HTML
//    @Async
//    public void sendWelcomeMail(User user) {
//        final Context ctx = new Context();
//        ctx.setVariable("user", user);
//        final String htmlContent = templateEngine.process("/mail/welcomeMail.html", ctx);
//
//        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//
//        try {
//            final MimeMessageHelper message;
//            message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
//            message.setSubject("Welcome to our WebSite");
//            message.setTo(user.getEmail());
//            message.setText(htmlContent, true);
//            try {
//                if (mainService.getImage(user.getPicName()) != null){
//                    message.addAttachment(user.getPicName(), new ByteArrayResource(mainService.getImage(user.getPicName())));
//                }
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            javaMailSender.send(mimeMessage);
//        } catch (MessagingException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
