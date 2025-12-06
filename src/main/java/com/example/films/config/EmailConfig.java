package com.example.films.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {


    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com"); 
        mailSender.setPort(587);
        mailSender.setUsername("nixieshimy@gmail.com");
        mailSender.setPassword("kfsr dbfw rugv ogjk"); 
        
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");    
        props.put("mail.debug", "true");
        
        return mailSender;
    }

    // @Bean
    // public JavaMailSender javaMailSender() {
    //     JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    //     mailSender.setHost("smtp.ethereal.email"); 
    //     mailSender.setPort(587);
    //     mailSender.setUsername("jared.ryan@ethereal.email");
    //     mailSender.setPassword("DnMqFBvw5BBgFuj6D5"); 
        
    //     Properties props = mailSender.getJavaMailProperties();
    //     props.put("mail.transport.protocol", "smtp");
    //     props.put("mail.smtp.auth", "true");
    //     props.put("mail.smtp.starttls.enable", "true");
    //     props.put("mail.debug", "true");
        
    //     return mailSender;
    // }
}