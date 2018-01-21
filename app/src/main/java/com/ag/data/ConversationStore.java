package com.ag.data;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.ag.BaseActivity;
import com.ag.Benchmarker;

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

	private ArrayList<Conversation> mConversations;
	private ArrayList<Message> messages;
	private ContentResolver mResolver;
	private LayoutInflater mInflater;
	private Cursor mCursor;
	private Adapter mAdapter;

	public ConversationStore() {
		mResolver = BaseActivity.getContext().getContentResolver();
		mConversations = new ArrayList<Conversation>(20);
		mInflater = LayoutInflater.from(BaseActivity.getContext());
		mCursor = mResolver.query(URI,
				PROJECTION,
				null,
				null,
				null
		);

		mCursor.registerContentObserver(new ChangeObserver());
		mAdapter = new Adapter();
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
			conv.threadId = mCursor.getLong(0);
			conv.date = mCursor.getLong(1);
			conv.msgCount = mCursor.getInt(2);
			conv.lastMsgId = mCursor.getInt(mCursor.getColumnIndex("_id"));
			conv.read = mCursor.getInt(5) == 1;

			if(!mConversations.contains(conv))
				mConversations.add(conv);

			int recipient_id = mCursor.getInt(3);
			Contact recipient = ContactStore.getByRecipientId(recipient_id);
			conv.contact = recipient;
		} while(mCursor.moveToNext());

		mAdapter.notifyDataSetChanged();
		Benchmarker.stop("ConvUpdate");
	}

	public List<Conversation> getAllConversations(){
		update();
		return mConversations;
	}

	public Conversation getConversation(int position) {
		return mConversations.get(position);
	}

	public void bindView(ListView listView) {
		listView.setAdapter(mAdapter);
	}

	private class Adapter extends BaseAdapter {
		@Override
		public int getCount() {
			return mConversations.size();
		}

		@Override
		public Object getItem(int position) {
			return mConversations.get(position);
		}

		@Override
		public long getItemId(int position) {
			return mConversations.get(position).threadId;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
//			if(convertView == null) {
//				convertView = mInflater.inflate(R.layout.conversation_list_item,
//						parent,
//						false);
//			}
//			ConversationActivity c = mConversations.get(position);
//			ConversationListItem entry = (ConversationListItem) convertView;
//			entry.bind(c);
//			return entry;
			return null;
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
