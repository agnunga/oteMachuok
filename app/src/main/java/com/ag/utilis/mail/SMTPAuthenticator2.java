package com.ag.utilis.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class SMTPAuthenticator2 extends Authenticator {
    public SMTPAuthenticator2() {

        super();
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        String username = "yolojeya@gmail.com";
        String password = "pzpoaarxutxasluw";

        if ((username != null) && (username.length() > 0) && (password != null)
                && (password.length() > 0)) {

            return new PasswordAuthentication(username, password);
        }

        return null;
    }
}