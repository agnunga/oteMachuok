package com.ag;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class ResponderService extends Service {
    //the action fired by the Android system when an SMS was received
    private static final String RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private static final String SENT_ACTION = "android.provider.Telephony.SENT_SMS";
    private static final String DELIVERED_ACTION = "android.provider.Telephony.DELIVERED_SMS";

    String requester;
    String smsBody;
    String reply="";
    SharedPreferences myprefs;

    @Override
    public void onCreate() {
        super.onCreate();
        myprefs = PreferenceManager.getDefaultSharedPreferences(this);

        registerReceiver(sentReceiver, new IntentFilter(SENT_ACTION));
        registerReceiver(deliverReceiver, new IntentFilter(DELIVERED_ACTION));

        IntentFilter filter = new IntentFilter(RECEIVED_ACTION);
        registerReceiver(receiver, filter);

        IntentFilter attemptedfilter = new IntentFilter(SENT_ACTION);
        registerReceiver(sender,attemptedfilter);
    }

    private BroadcastReceiver sender = new BroadcastReceiver(){
        @Override
        public void onReceive(Context c, Intent i) {
            if(i.getAction().equals(SENT_ACTION)) {
                if(getResultCode() != Activity.RESULT_OK) {
                    String recipient = i.getStringExtra("recipient");
//                    String smsBody = i.getStringExtra("body");
                    String smsBody = "Figuring The body out .... ";
                    requestReceived(recipient, smsBody);
                }
            }
        }
    };

    BroadcastReceiver sentReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context c, Intent in) {
            switch(getResultCode()) {
                case Activity.RESULT_OK:
                    smsSent(true, "Messola SMS sent");
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    smsSent(false, "SMS not sent ::: Generic failure");
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    smsSent(false,"SMS not sent ::: No service");
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    smsSent(false, "SMS not sent ::: Null PDU");
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    smsSent(false, "SMS not sent ::: Radio Off");
                    break;
                default:
                    smsSent(false, "SMS not sent ::: Default");
                    break;
            }
        }
    };

    public void smsSent(boolean sent, String meso) {
        if (sent) {
            Log.d(Messola.TAG, meso);
        }else {
            Log.e(Messola.TAG, meso);
        }
        Toast.makeText(Messola.getContext(), meso, Toast.LENGTH_LONG);
    }

    public void smsDelivered(boolean sent, String meso) {
        if (sent) {
            Log.d(Messola.TAG, meso);
        }else {
            Log.e(Messola.TAG, meso);
        }
        Toast.makeText(Messola.getContext(), meso, Toast.LENGTH_LONG);
    }

    /*public void smsDelivered() {
        Log.d(Messola.TAG, "Messola SMS delivered");
        Toast.makeText(this, "Messola SMS delivered", Toast.LENGTH_LONG);
    }*/

    BroadcastReceiver deliverReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context c, Intent in) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    smsDelivered(true, "SMS Delivered");
                    break;
                case Activity.RESULT_CANCELED:
                    smsDelivered(true, "SMS not delivered");
                default:
                    break;
            }
//            smsDelivered();
        }
    };

    public void requestReceived(String requester, String smsBody) {
        Log.d(Messola.TAG,"Recipient Identifier is ::::: " + requester);
        this.requester = requester;
        this.smsBody = smsBody;
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent in) {
            Log.v(Messola.TAG,"sms has been received (On Receive)");
            reply="";
            if(in.getAction().equals(RECEIVED_ACTION)) {
                Log.v(Messola.TAG,"sms has been received (On Receive)" + " :::::: On SMS RECEIVE");

                Bundle bundle = in.getExtras();
                if(bundle!=null) {
                    Object[] pdus = (Object[])bundle.get("pdus");
                    SmsMessage[] messages = new SmsMessage[pdus.length];
                    for(int i = 0; i<pdus.length; i++) {
                        Log.v(Messola.TAG,"sms has been received (On Receive)" + "FOUND MESSAGE");
                        messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                    }
                    for(SmsMessage message: messages) {
                        requestReceived(message.getOriginatingAddress(), message.getMessageBody());
                    }
                    respond();
                }
            }
        }
    };

    /*@Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }*/

    public void respond() {
        try {
            Messola.sendSMS("Thank you for reaching out, I will get back to you.", requester);
        }catch (Exception e){

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        unregisterReceiver(sender);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}