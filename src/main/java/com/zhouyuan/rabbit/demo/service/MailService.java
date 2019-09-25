package com.zhouyuan.rabbit.demo.service;

import com.zhouyuan.rabbit.demo.config.MailProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Properties;

@Service
/**
 * 邮件发送服务
 */
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    @Autowired
    MailProperties mailProperties;

    @Autowired
    Environment env;

    public void send(String addresses){

        Properties properties = new Properties();
        properties.setProperty("mail.host",mailProperties.getHost());
        properties.setProperty("mail.transport.protocol",mailProperties.getProtocol());
        properties.setProperty("mail.smtp.port",mailProperties.getPort());
        properties.setProperty("mail.smtp.auth",mailProperties.getNeedAuth());
        properties.setProperty("mail.smtp.socketFactory.class",mailProperties.getSslClass());

/*        Session session = Session.getDefaultInstance(properties);
        session.setDebug(true);*/ //写法①

        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailProperties.getUserName(),mailProperties.getPassword());
            }
        };
        Session session = Session.getInstance(properties,auth);//写法②

        MimeMessage mimeMessage = new MimeMessage(session);
        try {
            mimeMessage.setFrom(env.getProperty("mail.from"));//发件人
            mimeMessage.setSubject(env.getProperty("mail.subject"));//主题
            mimeMessage.setContent(env.getProperty("mail.content"),"text/html;charset=utf-8");//内容

            Arrays.asList(addresses.split(","))
                    .stream()
                    .forEach(address -> {
                        try {
                            mimeMessage.addRecipients(Message.RecipientType.TO,address);
                        } catch (MessagingException e) {
                            log.error("发送邮件时发生异常：{}",e);
                        }
                    });//收件人

            Arrays.asList(env.getProperty("mail.by").split(","))
                    .stream()
                    .forEach(address -> {
                        try {
                            mimeMessage.addRecipients(Message.RecipientType.CC,address);
                        } catch (MessagingException e) {
                            log.error("发送邮件时发生异常：{}",e);
                        }
                    });//抄送


            Transport transport = session.getTransport();
            transport.connect(mailProperties.getUserName(),mailProperties.getPassword());
            transport.sendMessage(mimeMessage,mimeMessage.getAllRecipients());
        } catch (Exception e) {
            log.error("发送邮件时发生异常：{}",e);
        }


    }
}
