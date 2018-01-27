package com.ag.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;

import com.ag.Messola;

import java.util.ArrayList;

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
//    private String mName;
    private long mThreadId;
//    private Adapter mAdapter;
    private ListView mListView;

    private ArrayList<Conv> mItems;
    private Cursor mCursor;

    public ConvStore(long threadId) {
        mContext = Messola.getContext();
        mResolver = mContext.getContentResolver();
//        mName = name;
        mThreadId = threadId;
        mItems = new ArrayList<Conv>(50);
//        mAdapter = new Adapter();
        // TODO: Make this query faster.
        mCursor = mResolver.query(URI,
                PROJECTION,
                "thread_id=" + mThreadId,
                null,
                "date ASC"
        );
        mCursor.registerContentObserver(new ChangeObserver());
    }

    /*public void bindView(ListView lv) {
        mListView = lv;
        lv.setAdapter(mAdapter);
    }*/

    public void update() {
        mCursor.requery();
        mItems.clear();

        mCursor.moveToFirst();
        do {
            Conv m = new Conv();

            m.set_id(mCursor.getInt(0));
            m.setThread_id(mCursor.getString(1));
            m.setAddress(mCursor.getString(2));
            m.setPerson(mCursor.getString(3));
            m.setDate(mCursor.getLong(4));
            m.setProtocol(mCursor.getString(5));
            m.setRead(mCursor.getString(6));
            m.setStatus(mCursor.getString(7));
            m.setType(mCursor.getString(8));
            m.setSubject(mCursor.getString(9));
            m.setBody(mCursor.getString(10));

            mItems.add(m);
        } while(mCursor.moveToNext());
        markThreadRead();
        mCursor.close();
    }

    private void markThreadRead() {
        Log.d("READ", "Marking conv as read");
        ContentValues cv = new ContentValues(1);
        cv.put("read", 1);

        mResolver.update(URI,
                cv,
                "read=0 AND thread_id=" + mThreadId,
                null
        );
    }

    public ArrayList<Conv> getAllMessage() {
        return mItems;
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
