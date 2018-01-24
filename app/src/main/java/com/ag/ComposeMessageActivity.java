package com.ag;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
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

            Contact c = new Contact(-1, name, number, R.drawable.userpic);
            mConv = new Conversation();
            mConv.setContact(c);
            mConv.setThreadId(threadId);
            // TODO: Need to remove the name parameter.
            mStore = new MessageStore(mConv.getThreadId(), mConv.getContact().getName());
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

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onClick(View v) {
            CharSequence text = mTextEditor.getText();
            if(text.length() == 0)
                return;
            mTextEditor.setText("");

            if(mConv == null) {
                Contact c = ContactStore.getByNumber(mSubject.getText().toString());
                long threadId = Telephony.Threads.getOrCreateThreadId(Messola.getContext(), c.getNumber());
                mConv = new Conversation();
                mConv.setContact(c);
                mConv.setThreadId(threadId);
            }
            sendMessage(mConv, text.toString());
            if(mStore == null) {
                mStore = new MessageStore(mConv.getThreadId(), mConv.getContact().getName());
                mStore.bindView(mHistory);
            }
			/*mStore.update();
			updateTitleBar();
			*/
            Log.d(TAG, "Go to the conversation itself....");
            startComposing(Messola.getContext(), mConv);
        }
    }

    private void updateTitleBar() {
        if(mConv == null) {
            mSubject.setVisibility(View.VISIBLE);
        }
        else {
            setTitle(mConv.getContact().getFormatted());
            mSubject.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(Messola.getContext(), MainActivity.class);
        startActivity(i);
    }
}
