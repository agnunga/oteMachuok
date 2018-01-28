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
    //    private String mName;
    private long mThreadId;
    //    private Adapter mAdapter;
    private ListView mListView;

    private List<Conv> convList;
    private Cursor mCursor;

    public ConvStore(long threadId) {
        mContext = Messola.getContext();
        mResolver = mContext.getContentResolver();
//        mName = name;
        mThreadId = threadId;
        convList = new ArrayList<Conv>(50);
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
//        mCursor.requery();
        convList.clear();
        mCursor.moveToFirst();
        int i= 0;
        do {
//            System.out.println("I PASSED HERE ++++ " + i++);
            Conv conv = new Conv();
            conv.set_id(mCursor.getInt(0));
            conv.setThread_id(mCursor.getString(1));
            conv.setAddress(mCursor.getString(2));
            conv.setPerson(mCursor.getString(3));
            conv.setDate(mCursor.getLong(4));
            conv.setProtocol(mCursor.getString(5));
            conv.setRead(mCursor.getString(6));
            conv.setStatus(mCursor.getString(7));
            conv.setType(mCursor.getString(8));
            conv.setSubject(mCursor.getString(9));
            conv.setBody(mCursor.getString(10));

//            convList.add(conv);
            if(!convList.contains(conv))
                convList.add(conv);

        } while(mCursor.moveToNext());
        markThreadRead();
//        mCursor.close();
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

    public List<Conv> getAllConvs() {
        update();
        return convList;
    }

    private class ChangeObserver extends ContentObserver {
        public ChangeObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {
//            update();
        }
    }
}
