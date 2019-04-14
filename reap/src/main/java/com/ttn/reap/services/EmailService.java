package com.ttn.reap.services;

import com.ttn.reap.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class EmailService {

    @Autowired
    public JavaMailSender javaMailSender;

    @Async
    public void sendMail(User user, String appUrl){
        SimpleMailMessage passwordResetEmail = new SimpleMailMessage();
        passwordResetEmail.setTo(user.getEmail());
        passwordResetEmail.setSubject("Password Reset Link");
        passwordResetEmail.setText("To reset your password, click the link below:\n" + appUrl
                + ":8080/reset?token=" + user.getResetToken());
        javaMailSender.send(passwordResetEmail);
    }

    @Async
    public void revokeMail(SimpleMailMessage email){
        javaMailSender.send(email);
    }
}
