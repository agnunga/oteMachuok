package com.ag.data;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;

import com.ag.BaseActivity;
import com.ag.Benchmarker;
import com.ag.Messola;

import java.util.ArrayList;
import java.util.List;

public class ConversationStore {
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

	private ArrayList<Conversation> mConversations;
	private ArrayList<Message> messages;
	private ContentResolver mResolver;
	private LayoutInflater mInflater;
	private Cursor mCursor;

	public ConversationStore() {
		mResolver = Messola.getContext().getContentResolver();
		mConversations = new ArrayList<Conversation>(20);
		mInflater = LayoutInflater.from(Messola.getContext());
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
		mConversations.clear();
		mCursor.requery();

		if(mCursor == null || mCursor.getCount() == 0) {
			return;
		}

		mCursor.moveToFirst();
		do {
			Conversation conv = new Conversation();
			conv.setThreadId(mCursor.getLong(0));
			conv.setDate(mCursor.getLong(1));
			conv.setMsgCount(mCursor.getString(2));
			conv.setRecipient_ids(mCursor.getString(3));
			conv.setSnippet(mCursor.getString(4));
			conv.setRead(mCursor.getInt(5));
			conv.setType(mCursor.getString(6));

			if(!mConversations.contains(conv))
				mConversations.add(conv);

			int recipient_id = mCursor.getInt(3);
			Contact recipient = ContactStore.getByRecipientId(recipient_id);
			conv.setContact(recipient);
		} while(mCursor.moveToNext());

//		Benchmarker.stop("ConvUpdate");
	}

	public List<Conversation> getAllConversations(){
		update();
		return mConversations;
	}

	public Conversation getConversation(int position) {
		return mConversations.get(position);
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
