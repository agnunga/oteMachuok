package com.ag;

import com.ag.data.Chat;
import com.ag.data.ChatStore;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class Messola extends android.app.Application {
    public static String TAG = "Messola";
    private static Messola instance;
    private static ContentResolver contentResolver;

    private static final String RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private static final String SENT_ACTION = "android.provider.Telephony.SENT_SMS";
    private static final String DELIVERED_ACTION = "android.provider.Telephony.DELIVERED_SMS";

    public Messola() {
        instance = this;
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    public static ChatStore getConversationStore() {
        // TODO: Implement it this way.
        return null;
    }

    public static void sendSMS(String reply, String requester) throws Exception{
        Log.v(Messola.TAG,"Sending SMS to " + requester);

        SmsManager smsManager = SmsManager.getDefault();
        Intent sentIntent = new Intent(SENT_ACTION);
        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(getContext(),0,sentIntent,0);

        Intent deliverIn = new Intent(DELIVERED_ACTION);
        PendingIntent deliverPIn = PendingIntent.getBroadcast(getContext(), 0, deliverIn,0);
        ArrayList<String> msgPieces = smsManager.divideMessage(reply);
        ArrayList<PendingIntent> sentIns = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> deliverIns = new ArrayList<PendingIntent>();

        for(int i = 0; i < msgPieces.size(); i++) {
            sentIns.add(sentPendingIntent);
            deliverIns.add(deliverPIn);
        }

        smsManager.sendMultipartTextMessage(requester, null, msgPieces, sentIns, deliverIns);
    }

    public static boolean sendMessage2(String recipient, String message) {
        try {
            sendSMS(message, recipient);

            //After sending Now update the SMS
            ContentValues values = new ContentValues(7);
//            values.put("address", mConv.getContact().getNumber());
//            values.put("thread_id", mConv.getThreadId());
            values.put("read", true);
            values.put("subject", "");
            values.put("body", message);
            values.put("type", 2);

            Uri uri = Uri.parse("content://sms/outbox");
            instance.getContentResolver().insert(uri, values);

            showToast("Sending message: " + message + "... ");
            return true;
        }catch (Exception e){
            e.printStackTrace();
            Log.d (TAG, "" + recipient + ") + Body " + message + "... ");
            showToast("Wrong Address: (" + recipient + ")");
            return false;
        }
    }

    public static void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
    }

}