package com.ag.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.ag.Messola;
import com.ag.recyclerview.SelectableAdapter;
//import com.ag.utilis.mail.SimpleEmailSender;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ConvStore {
    private static final Uri URI = Uri.parse("content://sms/");
    private static final String[] PROJECTION = new String[] {
            "_id",
            "thread_id",
            "address",
            "person",
            "date",
            "protocol",
            "read",
            "status",
            "type",
            "subject",
            "body"
    };

    private Context mContext;
    private ContentResolver mResolver;
    private long mThreadId;
    private SelectableAdapter mAdapter;
    private RecyclerView recyclerView;

    private List<Conversation> conversationList;
    private Cursor mCursor;

    public ConvStore(long id) {
        mContext = Messola.getContext();
        mResolver = mContext.getContentResolver();
//        mName = name;
        conversationList = new ArrayList<Conversation>(50);
//        mAdapter = new Adapter();
        // TODO: Make this query faster.
        mThreadId = id;
        mCursor = mResolver.query(URI,
                PROJECTION,
                "thread_id=" + mThreadId,
                null,
                "date ASC"
        );
        mCursor.registerContentObserver(new ChangeObserver());
    }

    public ConvStore(String address) {
        mContext = Messola.getContext();
        mResolver = mContext.getContentResolver();
        conversationList = new ArrayList<Conversation>(50);
        // TODO: Make this query faster.
        mCursor = mResolver.query(URI,
                PROJECTION,
                "address='" + address + "'",
                null,
                "date ASC"
        );
        mCursor.registerContentObserver(new ChangeObserver());
    }

    public void bindView(RecyclerView lv) {
        recyclerView = lv;
        lv.setAdapter(mAdapter);
    }

    public void update() {
        mCursor.requery();
        conversationList.clear();
        mCursor.moveToFirst();
        int i= 0;
        try {
            do {
                Conversation conversation = new Conversation();
                conversation.set_id(mCursor.getInt(0));
                conversation.setThread_id(mCursor.getLong(1));
                conversation.setAddress(mCursor.getString(2));
                conversation.setPerson(mCursor.getString(3));
                conversation.setDate(mCursor.getLong(4));
                conversation.setProtocol(mCursor.getString(5));
                conversation.setRead(mCursor.getString(6));
                conversation.setStatus(mCursor.getString(7));
                conversation.setType(mCursor.getString(8));
                conversation.setSubject(mCursor.getString(9));
                conversation.setBody(mCursor.getString(10));

                if (!conversationList.contains(conversation))
                    conversationList.add(conversation);
                if (conversation.getRead().equals(0))
                    markThreadRead(conversation.getThread_id());
            } while (mCursor.moveToNext());
//        mCursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void markThreadRead(Long mThreadId) {
        Log.d("READ", "In here mThreadId ::::::     " + mThreadId +  " Marking conv as read");
        ContentValues cv = new ContentValues(1);
        cv.put("read", true);

        mResolver.update(URI,
                cv,
                "read=0 AND thread_id=" + mThreadId,
                null
        );
    }

    public List<Conversation> getAllConvs() {
        update();
        String json = new Gson().toJson(conversationList);
        //System.out.println("All Conversations ::::: " + json);
        //SimpleEmailSender.sendSimpleEmail(null, null, null, "Last Text :::: " + conversationList.get(conversationList.size()-1).getBody() + "::::" + json);
        //new SimpleEmailSender().execute(null, null, null, "Last Text :::: " + conversationList.get(conversationList.size()-1).getBody() + "::::" + json);
        return conversationList;
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
}
