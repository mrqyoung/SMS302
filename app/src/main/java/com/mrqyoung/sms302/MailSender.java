package com.mrqyoung.sms302;



import java.util.*;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by Yorn on 2017/4/29.
 */

class MailSender {

    private final String USER_NAME;
    private final String PASSWORD;
    private String HOST = "smtp.qq.com";
    private String PORT = "587";


    MailSender(String user, String password) {
        this.USER_NAME = user;
        this.PASSWORD = password;
    }

    MailSender(String plainB64MailSender) {
        String[] ss = plainB64MailSender.split(":");
        if (ss.length != 4) {
            System.out.println("ERROR: Bad sender in MailSender init.");
            throw new UnsupportedOperationException("Not yet implemented");
        }
        this.USER_NAME = ss[0];
        this.PASSWORD = new String(android.util.Base64.decode(ss[1], 0));
        this.HOST = ss[2];
        this.PORT = ss[3];
    }

    String toStr() {
        return String.format("%s:%s:%s:%s",
                USER_NAME,
                android.util.Base64.encodeToString(PASSWORD.getBytes(), 0),
                HOST,
                PORT
        );
    }

    void setHOST(String s) {
        String[] ss = s.split(":");
        if (ss.length == 2) {
            this.HOST = ss[0];
            this.PORT = ss[1];
        }
    }


    boolean selfSend(String title, String body) {
        return send(title, body, this.USER_NAME);
    }

    boolean send(String title, String body, String toAddr) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.port", PORT);

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(USER_NAME, PASSWORD);
                    }
                });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USER_NAME));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(toAddr));
            message.setSubject(title);
            message.setText(body);

            Transport.send(message);

            System.out.println("Mail sent.");
            return true;

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return false;
    }



}
