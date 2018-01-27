package com.ag.data;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;

import com.ag.BaseActivity;
import com.ag.Benchmarker;
import com.ag.Messola;

import java.util.ArrayList;
import java.util.List;

public class ChatStore {
    private static final String TAG = BaseActivity.TAG + "/ConvStore";
    private static final String[] PROJECTION = new String[] {
            "_id",
            "date",
            "message_count",
            "recipient_ids",
            "snippet",
            "read",
            "type"
    };

    private static final Uri URI = Uri.parse("content://mms-sms/conversations?simple=true");
//	private static final Uri URI = Uri.parse("content://mms-sms/conversations/");

    private ArrayList<Chat> mChats;
//    private ArrayList<Conv> convs;
    private ContentResolver mResolver;
//    private LayoutInflater mInflater;
    private Cursor mCursor;

    public ChatStore() {
        mResolver = Messola.getContext().getContentResolver();
        mChats = new ArrayList<Chat>();
//        mInflater = LayoutInflater.from(Messola.getContext());
        mCursor = mResolver.query(URI,
                PROJECTION,
                null,
                null,
                "date desc"
        );

        mCursor.registerContentObserver(new ChangeObserver());
    }

    public void update() {
        Benchmarker.start("ConvUpdate");
        mChats.clear();
//        mCursor.requery();
        if(mCursor == null || mCursor.getCount() == 0) {
            return;
        }

        mCursor.moveToFirst();
        do {
            Chat conv = new Chat();
            conv.setThreadId(mCursor.getLong(0));
            conv.setDate(mCursor.getLong(1));
            conv.setMsgCount(mCursor.getString(2));
            conv.setRecipient_ids(mCursor.getString(3));
            conv.setSnippet(mCursor.getString(4));
            conv.setRead(mCursor.getInt(5));
            conv.setType(mCursor.getString(6));


            if(!mChats.contains(conv))
                mChats.add(conv);
            int recipient_id = mCursor.getInt(3);
            Contact recipient = ContactStore.getByRecipientId(recipient_id);
            conv.setContact(recipient);
            if(conv.getRead() == 0) {
                conv.setUnreadCount(unreadSms(conv.getContact().getNumber()));
            }
            Log.d(TAG,  conv.getRead() + "\t" +
                    conv.getUnreadCount()+ " / " +conv.getMsgCount()+"\t"+
                    conv.getContact().getName()
            );
        } while(mCursor.moveToNext());
        mCursor.close();
        Benchmarker.stop("ConvUpdate");
    }

    public List<Chat> getAllConversations(){
        update();
        return mChats;
    }

    public Chat getConversation(int position) {
        return mChats.get(position);
    }

    private class ChangeObserver extends ContentObserver {
        public ChangeObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {
            update();
        }
    }

    public int unreadSms(String number) {
        int unreadcount = 0;
        String address = number;
        Cursor unreadcountcursor = Messola.getContext().getContentResolver().query(Uri.parse("content://sms/inbox"),
                new String[]{}, "read = 0 and address='"+address+"'", null, null);

        unreadcount = unreadcountcursor.getCount();
        unreadcountcursor.close();
        return unreadcount;
    }
}
