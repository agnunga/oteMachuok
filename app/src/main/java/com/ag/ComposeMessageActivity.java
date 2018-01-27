package com.ag;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ag.data.Contact;
import com.ag.data.ContactStore;
import com.ag.data.Chat;
import com.ag.data.ConvStore;

public class ComposeMessageActivity extends BaseActivity {
    private ListView mHistory;
    private TextView mSubject;
    private TextView mTextEditor;
    private Button mSendButton;

    private ConvStore convStore;
    private Chat chatItem;

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
            chatItem = new Chat();
            chatItem.setContact(c);
            chatItem.setThreadId(threadId);
            // TODO: Need to remove the name parameter.
            convStore = new ConvStore(chatItem.getThreadId());
//            convStore.bindView(mHistory);
            convStore.update();
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

            if(chatItem == null) {
                Contact c = ContactStore.getByNumber(mSubject.getText().toString());
                long threadId = Telephony.Threads.getOrCreateThreadId(Messola.getContext(), c.getNumber());
                chatItem = new Chat();
                chatItem.setContact(c);
                chatItem.setThreadId(threadId);
            }
            sendMessage(chatItem, text.toString());
            if(convStore == null) {
                convStore = new ConvStore(chatItem.getThreadId());
//                convStore.bindView(mHistory);
            }
			/*convStore.update();
			updateTitleBar();
			*/
            Log.d(TAG, "Go to the conversation itself....");
            startComposing(Messola.getContext(), chatItem);
        }
    }

    private void updateTitleBar() {
        if(chatItem == null) {
            mSubject.setVisibility(View.VISIBLE);
        }
        else {
            setTitle(chatItem.getContact().getFormatted());
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
