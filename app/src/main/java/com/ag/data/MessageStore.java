package com.ag.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.ag.BaseActivity;
import com.ag.Messola;

import java.util.ArrayList;

public class MessageStore {
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
    private String mName;
    private long mThreadId;
    private Adapter mAdapter;
    private ListView mListView;

    private ArrayList<Message> mItems;
    private Cursor mCursor;

    public MessageStore(long threadId, String name) {
        mContext = Messola.getContext();
        mResolver = mContext.getContentResolver();
        mName = name;
        mThreadId = threadId;
        mItems = new ArrayList<Message>(50);
        mAdapter = new Adapter();
        // TODO: Make this query faster.
        mCursor = mResolver.query(URI,
                PROJECTION,
                "thread_id=" + mThreadId,
                null,
                "date ASC"
        );
        mCursor.registerContentObserver(new ChangeObserver());
    }

    public void bindView(ListView lv) {
        mListView = lv;
        lv.setAdapter(mAdapter);
    }

    public void update() {
        mCursor.requery();
        mItems.clear();

        mCursor.moveToFirst();
        do {
            Message m = new Message();

            m._id = mCursor.getInt(0);
            m.thread_id = mCursor.getString(1);
            m.address = mCursor.getString(2);
            m.person = mCursor.getString(3);
            m.date = mCursor.getLong(4);
            m.protocol = mCursor.getString(5);
            m.read = mCursor.getString(6);
            m.status = mCursor.getString(7);
            m.type = mCursor.getString(8);
            m.subject = mCursor.getString(9);
            m.body = mCursor.getString(10);

            mItems.add(m);
        } while(mCursor.moveToNext());

        markThreadRead();
        mAdapter.notifyDataSetChanged();
//		mListView.setSelection(mItems.size() - 1);
    }

    private void markThreadRead() {
        ContentValues cv = new ContentValues(1);
        cv.put("read", 1);

        mResolver.update(URI,
                cv,
                "read=0 AND thread_id=" + mThreadId,
                null
        );
    }

    private Spannable formatMessage(Message msg) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        if(msg.type == Message.T_INBOUND) {
            sb.append(mName);
        }
        else
            sb.append("Me");
        sb.append(": ");
        sb.append(msg.body);

        // TODO: Refactor the shit out of this method.
        sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                0,
                msg.type == "1" ? mName.length() + 1 : 3,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        return sb;
    }

    public ArrayList<Message> getAllMessage() {
        return mItems;
    }

    private class Adapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view;
            if(convertView == null) {
                view = new TextView(mContext);
                view.setTextSize(16);
                view.setTextColor(0xffffffff);
            }
            else {
                view = (TextView) convertView;
            }
            Message msg = mItems.get(position);
            view.setText(formatMessage(msg), BufferType.SPANNABLE);

            return view;
        }
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
