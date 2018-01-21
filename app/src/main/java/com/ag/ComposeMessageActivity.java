package com.ag;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ag.data.Contact;
import com.ag.data.ContactStore;
import com.ag.data.Conversation;
import com.ag.data.MessageStore;

public class ComposeMessageActivity extends BaseActivity {
    private ListView mHistory;
	private TextView mSubject;
	private TextView mTextEditor;
	private Button mSendButton;
	
	private MessageStore mStore;
	private Conversation mConv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.compose_message_activity);

		
		// Initialize GUI elements.
		mHistory = (ListView) findViewById(R.id.history);
		mSubject = (TextView) findViewById(R.id.subject);
		mTextEditor = (TextView) findViewById(R.id.text_editor);
		mSendButton = (Button) findViewById(R.id.send_button);
		mSendButton.setOnClickListener(new SendListener(this));
		
		Intent i = getIntent();
		if(i.hasExtra("thread_id")) {
			long threadId = i.getLongExtra("thread_id", -1);
			String name = i.getStringExtra("name");
			String number = i.getStringExtra("number");
			
			Contact c = new Contact(-1, name, number);
			mConv = new Conversation();
			mConv.contact = c;
			mConv.threadId = threadId;
			
			// TODO: Need to remove the name parameter.
			mStore = new MessageStore(mConv.threadId, mConv.contact.name);
			mStore.bindView(mHistory);
			mStore.update();
		}
		
		updateTitleBar();
	}
	
	@Override
	protected void onPause() {
		Log.d("SimpleSMS", "Called onPause()!");
		ContactStore.exportCache();
		super.onPause();
	}

	@Override
	protected void onResume() {
		Log.d("SimpleSMS", "Called onResume()!");
		super.onResume();
	}
	
	private class SendListener implements OnClickListener {
		private Context mContext;
		
		public SendListener(Context c) {
			mContext = c;
		}
		
		@Override
		public void onClick(View v) {
			CharSequence text = mTextEditor.getText();
			if(text.length() == 0)
				return;
			mTextEditor.setText("");
			
			if(mConv == null) {
				Contact c = ContactStore.getByNumber(mSubject.getText().toString());
				long threadId = 0;
				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
					threadId = Telephony.Threads.getOrCreateThreadId(BaseActivity.getContext(), c.number);
				}
				mConv = new Conversation();
				mConv.contact = c;
				mConv.threadId = threadId;
			}
			sendMessage(text.toString());
			if(mStore == null) {
				mStore = new MessageStore(mConv.threadId, mConv.contact.name);
				mStore.bindView(mHistory);
			}
			mStore.update();
			updateTitleBar();
		}
	}

	private void sendMessage(String message) {
		// Send the message
		ContentValues values = new ContentValues(7);
		values.put("address", mConv.contact.number);
		values.put("read", false);
		values.put("subject", "");
		values.put("body", message);
		values.put("thread_id", mConv.threadId);
		values.put("type", 2);

		Uri uri = Uri.parse("content://sms/outbox");
		getContentResolver().insert(uri, values);

		Toast.makeText(BaseActivity.getContext(), "Sending message: " + message, Toast.LENGTH_SHORT).show();
		// TODO: Handle undelivered messages, etc.
		SmsManager.getDefault().sendTextMessage(mConv.contact.number,
				null,
				 message.toString(),
				null,
				null);
	}
	
	private void updateTitleBar() {
		if(mConv == null) {
			mSubject.setVisibility(View.VISIBLE);
		}
		else {
			setTitle(mConv.contact.getFormatted());
			mSubject.setVisibility(View.GONE);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent i = new Intent(getContext(), MainActivity.class);
		startActivity(i);
	}
}
