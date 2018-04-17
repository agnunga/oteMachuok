/*
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

public class ComposeSmsActivity extends BaseActivity {
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
        Log.d(TAG, "Called onResume()!");
        super.onResume();

        final String myPackageName = getPackageName();
        if (!Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName)) {
            // App is not default.
            // Show the "not currently set as the default SMS app" interface
            View viewGroup = findViewById(R.id.not_default_app);
            viewGroup.setVisibility(View.VISIBLE);

            // Set up a button that allows the user to change the default SMS app
            Button button = (Button) findViewById(R.id.change_default_app);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent =
                            new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                    intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                            myPackageName);
                    startActivity(intent);
                }
            });
        } else {
            // App is the default.
            // Hide the "not currently set as the default SMS app" interface
            View viewGroup = findViewById(R.id.not_default_app);
            viewGroup.setVisibility(View.GONE);
        }
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

            */
/*mSubject.setText("");
            mTextEditor.setText("");*//*


            if(chatItem == null) {
                */
/*Contact c = ContactStore.getByNumber(mSubject.getText().toString());
                long threadId = Telephony.Threads.getOrCreateThreadId(Messola.getContext(), c.getNumber());
                chatItem = new Chat();
                chatItem.setContact(c);
                chatItem.setThreadId(threadId);*//*

            }
            Log.d(Messola.TAG, "Value of the chatItem ::::: " + chatItem.toString());
            //boolean sent = Messola.sendMessage2(chatItem, text.toString());
            if(convStore == null) {
                convStore = new ConvStore(chatItem.getThreadId());
            }
            if(sent){
                Log.d(TAG, "Go to the conversation itself....");
                goToConversation(Messola.getContext(), chatItem);
            }
            return;
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
       */
/* super.onBackPressed();
        Intent i = new Intent(Messola.getContext(), MainActivity.class);
        startActivity(i);*//*

       finish();
    }
}
*/
