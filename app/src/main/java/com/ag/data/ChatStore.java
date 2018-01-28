package com.ag.data;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ag.BaseActivity;
import com.ag.Benchmarker;
import com.ag.Messola;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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

    private List<Chat> mChats;
    //    private ArrayList<Conv> convs;
    private ContentResolver mResolver;
    //    private LayoutInflater mInflater;
    private Cursor mCursor;

    public ChatStore() {
        mResolver = Messola.getContext().getContentResolver();
        mChats = new ArrayList<>();
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
        mCursor.requery();
        if(mCursor == null || mCursor.getCount() == 0) {
            return;
        }

        mCursor.moveToFirst();
        do {
            Chat chatItem = new Chat();
            chatItem.setThreadId(mCursor.getLong(0));
            chatItem.setDate(mCursor.getLong(1));
            chatItem.setMsgCount(mCursor.getString(2));
            chatItem.setRecipient_ids(mCursor.getString(3));
            chatItem.setSnippet(mCursor.getString(4));
            chatItem.setRead(mCursor.getInt(5));
            chatItem.setType(mCursor.getString(6));


            if(!mChats.contains(chatItem))
                mChats.add(chatItem);

            int recipient_id = mCursor.getInt(3);
            Contact recipient = ContactStore.getByRecipientId(recipient_id);
            chatItem.setContact(recipient);
            if(chatItem.getRead() == 0) {
                chatItem.setUnreadCount(unreadSms(chatItem.getContact().getNumber()));
                Log.d(TAG,  chatItem.getRead() + "\t" +
                        chatItem.getUnreadCount()+ " / " +chatItem.getMsgCount()+"\t"+
                        chatItem.getContact().getName()
                );
            }
        }while(mCursor.moveToNext());
//        mCursor.close();
        Benchmarker.stop("ConvUpdate");
    }

    public List<Chat> getAllChats(){
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
