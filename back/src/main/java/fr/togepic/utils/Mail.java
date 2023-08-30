package fr.togepic.utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

public abstract class Mail {

    public static final void send(String subject, String text, String to) {
        Properties props = new Properties();
        try {
            props.load(Mail.class.getResourceAsStream("/smtp.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Properties properties = System.getProperties();
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.starttls.required", "true");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
        for (String key : props.stringPropertyNames()) {
            if (key != null)
                properties.put(key, props.getProperty(key));
        }

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(properties.getProperty("mail.smtp.user"),
                        properties.getProperty("mail.smtp.password"));
            }
        });

        transport(session, subject, text, properties.getProperty("mail.smtp.user"), to);
    }

    private static void transport(Session session, String subject, String text, String from, String to) {
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(text, "text/html; charset=utf-8");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            message.setContent(multipart);
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
