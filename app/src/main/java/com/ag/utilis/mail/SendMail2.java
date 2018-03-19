package com.ag.utilis.mail;

import android.os.AsyncTask;
import android.util.Log;

import com.ag.Messola;

public class SendMail2 extends AsyncTask<String, Integer, Void> {
        //private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressDialog = ProgressDialog.show(Messola.getContext(), "Please wait", "Sending mail", true, false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //progressDialog.dismiss();
        }

        protected Void doInBackground(String... params) {

            String userName = "yolojeya@gmail.com";
            String password = "pzpoaarxutxasluw";

            String[] to = {"agunga3d@gmail.com"};
            String from = "yolojeya@gmail.com";
            String subj = "Trial 101";
            String body = "Body 101";

            /*String userName = params[0];
            String password = params[1];
            String to   = params[2];
            String from = params[3];
            String subj = params[4];
            String body = params[5];*/

            Mail2 m = new Mail2(userName, password);
            String[] toArr = to;
            m.setTo(toArr);
            m.setFrom(from);
            m.setSubject(subj);
            m.setBody(body);

            try {
                if(m.send()) {
                    Messola.showToast("Email was sent successfully.");
                    Log.e(Messola.TAG, "Mail2 sent");
                } else {
                    Messola.showToast("Email was not sent.");
                    Log.e(Messola.TAG, "Mail2 not sent");

                }
            } catch(Exception e) {
                Log.e("MailApp", "Could not send email", e);
            }
            return null;
        }
    }
