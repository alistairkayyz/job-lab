package com.dso34bt.jobportal.utilities;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public final class Email {
    public static boolean send(String to, String subject, String composedMessage){
        System.out.println("Sending a request and an Email...");
        Properties props = new Properties();
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "*");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("alistairdummy101@gmail.com", "kkopkfofpwapbgqw");
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress( "no-reply@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(composedMessage);

            Transport.send(message);

            return true;

        }
        catch (MessagingException e) {
            System.err.println(e.getMessage());

            return false;
        }
    }
}
