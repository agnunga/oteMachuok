/*
package com.ag.utilis.mail;

import android.os.AsyncTask;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

// Java program to send simple email using apache commons email
// Uses the Gmail SMTP servers
public class SimpleEmailSender extends AsyncTask<String, Integer, Void> {
    private static final String HOST = "smtp.gmail.com";
    private static final int PORT = 587;
    private static final boolean SSL_FLAG = true;
*/
/*
    public static void main(String[] args) {
        SimpleEmailSender.sendSimpleEmail(null, null, null, "Hello from Apache Mail2");
    }*//*


    public static void sendSimpleEmail(String fromAddress, String toAddress, String subject, String message) {

        String userName = "yolojeya@gmail.com";
        String password = "pzpoaarxutxasluw";

        if(fromAddress == null || fromAddress.trim().length() <= 0)
            fromAddress="yolojeya@gmail.com";

        if(toAddress == null || toAddress.trim().length() <= 0)
            toAddress =  "agunga3d@gmail.com";

        if(subject == null || subject.trim().length() <= 0)
            subject = "Messola Notifications";

        try {
            Email email = new SimpleEmail();
            email.setHostName(HOST);
            email.setSmtpPort(PORT);
            email.setAuthenticator(new DefaultAuthenticator(userName, password));
            email.setSSLOnConnect(SSL_FLAG);
            email.setFrom(fromAddress);
            email.setSubject(subject);
            email.setMsg(message);
            email.addTo(toAddress);
            email.send();
        }catch(Exception ex){
            System.out.println("Unable to send email");
            System.out.println(ex);
            ex.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(String... strings) {
        String fromAddress = strings[0];
        String toAddress = strings[1];
        String subject = strings[2];
        String message = strings[3];
        sendSimpleEmail(fromAddress, toAddress, subject, message);
        return null;
    }
}*/
