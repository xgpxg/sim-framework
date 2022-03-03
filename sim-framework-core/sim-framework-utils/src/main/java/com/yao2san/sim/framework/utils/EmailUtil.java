package com.yao2san.sim.framework.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * 邮箱工具
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "sim.utils.email")
@Data
public class EmailUtil {
    private static String username;
    private static String password;
    private static String host;
    private static String port;
    private static String protocol;

    @Value("sim.utils.email.username")
    public void setUsername(String username) {
        EmailUtil.username = username;
    }

    @Value("sim.utils.email.host")
    public void setHost(String host) {
        EmailUtil.host = host;
    }

    @Value("sim.utils.email.password")
    public void setPassword(String password) {
        EmailUtil.password = password;
    }

    @Value("sim.utils.email.port:25")
    public void setPort(String port) {
        EmailUtil.port = port;
    }

    @Value("sim.utils.email.protocol:'SMTP'")
    public void setProtocol(String protocol) {
        EmailUtil.protocol = protocol;
    }

    /**
     * 发送邮件
     *
     * @param emailAddr 邮箱地址
     * @param title     邮件的标题
     * @param emailMsg  邮件信息
     */
    public static Map<String, String> sendMail(String emailAddr, String title, String emailMsg) {
        Map<String, String> sendRes = new HashMap<>();

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", protocol);

        props.setProperty("mail.host", host);
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.port", port);
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.port", "465");

        Authenticator auth = new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };
        Session session = Session.getInstance(props, auth);

        Message message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(emailAddr));
            message.setRecipient(RecipientType.TO, new InternetAddress(emailAddr));

            message.setSubject(title);

            message.setContent(emailMsg, "text/html;charset=utf-8");

            Transport.send(message);

            sendRes.put("status", "ok");
            sendRes.put("msg", "");
            return sendRes;
        } catch (MessagingException e) {
            log.error("发送失败", e);
            sendRes.put("status", "fail");
            sendRes.put("msg", "发送失败:" + e.getMessage());
            return sendRes;
        }

    }

}
