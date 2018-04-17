package com.ag;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ag.data.ContactStore;
import com.ag.data.Conversation;
import com.ag.data.Contact;
import com.ag.data.Chat;
import com.ag.data.ConvStore;
import com.ag.recylcerconv.ConversationRecyclerView;
import com.ag.utilis.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConversationActivity extends BaseActivity {

    ContactStore contactStore = new ContactStore();

    private RecyclerView mRecyclerView;
    private ConversationRecyclerView mAdapter;
    private EditText newSmsEt;
    private Button send;
    private List<Conversation> conversationItems;
    private ListView mHistory;
    private TextView mSubject;
    private TextView mTextEditor;
    private Button mSendButton;
    private ConvStore convStore;
    private Chat chatItem;
    private boolean markReadInDefaultSmsApp;
    private String smsNumber;
    private String smsBoby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        conversationItems = new ArrayList<>();
        setContentView(R.layout.activity_conversation);
        //markReadInDefaultSmsApp = true;

        Intent i = getIntent();
        //i.FLAG_ACTIVITY_REORDER_TO_FRONT;
        Boolean hasConvId = i.hasExtra("thread_id");
        Boolean hasContactId = i.hasExtra("contact_id");
        if(hasConvId || hasContactId) {
            long threadId = i.getLongExtra("thread_id", -1);
            long contactId = i.getLongExtra("contact_id", -1);
            smsNumber = i.getStringExtra("number");
            String name = i.getStringExtra("name");

            Contact c = new Contact(contactId, name, smsNumber, contactStore.loadImage(contactId));
            chatItem = new Chat();
            chatItem.setContact(c);
            chatItem.setThreadId(threadId);
            //System.out.println("CHAT ITEM ::::::::::: " + chatItem.toString());
            if (hasConvId) {
                convStore = new ConvStore(threadId);
            }
            else {
                convStore = new ConvStore(smsNumber);
            }

            //convStore.update();
            setupToolbarWithUpNav(R.id.toolbar, chatItem.getContact().getName(), R.drawable.ic_action_back);
            conversationItems = convStore.getAllConvs();
            System.out.println("SIZE conversationItems =============== " + conversationItems.size());

            //Read SMS
            smsBoby = conversationItems.get(conversationItems.size()-1).getBody();
            markMessageRead(smsNumber, smsBoby);
            Log.w("ToBeMarkRead", "The number ::: " + smsNumber + " Body ::: " + smsBoby);

            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            mRecyclerView.setHasFixedSize(false);
            mRecyclerView.setLayoutManager(linearLayoutManager);
            //linearLayoutManager.setReverseLayout(true);
            mAdapter = new ConversationRecyclerView(this, conversationItems);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
            /*mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
                }
            }, 500);*/

            newSmsEt = (EditText) findViewById(R.id.et_message);
            newSmsEt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
                        }
                    }, 500);
                }
            });
            send = (Button) findViewById(R.id.bt_send);

            final String final_number = smsNumber;
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (newSmsEt.getText().length() > 0){
                        //Send and save sms
                        /*selectSim(final_number);*/
                        Messola.sendMessage2(final_number, newSmsEt.getText().toString());
                        List<Conversation> conversationList = new ArrayList<>();
                        Conversation conversationItem = new Conversation();
                        conversationItem.setDate(new Date().getTime());
                        conversationItem.setType("2");
                        conversationItem.setBody(newSmsEt.getText().toString());
                        conversationList.add(conversationItem);
                        mAdapter.addItem(conversationList);
                        mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
                        newSmsEt.setText("");
                    }
                }
            });
        }else {
          /*  Log.i(TAG, "Has no thread id");
            setContentView(R.layout.compose_message_activity);
            mHistory = (ListView) findViewById(R.id.history);
            mSubject = (TextView) findViewById(R.id.subject);
            mTextEditor = (TextView) findViewById(R.id.text_editor);
            mSendButton = (Button) findViewById(R.id.send_button);
            mSendButton.setOnClickListener(new SendListener(this));

            updateTitleBar();
            */
            onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_userphoto, menu);
        return true;
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

    private class SendListener implements View.OnClickListener {
        private Context mContext;

        public SendListener(Context c) {
            mContext = c;
        }

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onClick(View v) {
            String text = mTextEditor.getText().toString();
            String recipient = mSubject.getText().toString();
            if(text.length() == 0)
                return;

            /*mSubject.setText("");
            mTextEditor.setText("");*/

            if(chatItem == null) {
                Contact c = contactStore.getByNumber(mSubject.getText().toString());
                long threadId = Telephony.Threads.getOrCreateThreadId(Messola.getContext(), c.getNumber());
                chatItem = new Chat();
                chatItem.setContact(c);
                chatItem.setThreadId(threadId);
            }
            //Log.d(Messola.TAG, "Value of the chatItem ::::: " + chatItem.toString());
            boolean sent = Messola.sendMessage2(recipient, text.toString());
            if(convStore == null) {
                convStore = new ConvStore(chatItem.getThreadId());
            }
            if(sent){
                //Log.d(TAG, "Go to the conversation itself...." + chatItem.toString());
                //goToConversation(ConversationActivity.this, chatItem);
                Intent i = new Intent(ConversationActivity.this, MainActivity.class);
                i.putExtra("frgToLoad", 1);
                startActivity(i);
            }
            return;
        }
    }


    @Override
    public void onBackPressed() {
        if (markReadInDefaultSmsApp) {
            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.putExtra("address", smsNumber);
            //smsIntent.putExtra("sms_body","Body of Message");
            startActivity(smsIntent);
            markReadInDefaultSmsApp = false;
        }

        super.onBackPressed();
        finish();
       /* Intent i = new Intent(this, MainActivity.class);
        startActivity(i);*/
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        defaultAppResolve();
    }

    public void markMessageRead(String number, String body) {
        Log.w("Mark Read", "Marking: number ::: " + number + " Body ::: " + body);
        Uri uri = Uri.parse("content://sms/inbox");
        Cursor cursor = Messola.getContext().getContentResolver().query(uri, null, null, null, null);
        try{

            while (cursor.moveToNext()) {
                if ((cursor.getString(cursor.getColumnIndex("address")).equals(number)) && (cursor.getInt(cursor.getColumnIndex("read")) == 0)) {
                    if (cursor.getString(cursor.getColumnIndex("body")).startsWith(body)) {
                        String SmsMessageId = cursor.getString(cursor.getColumnIndex("_id"));
                        ContentValues values = new ContentValues();
                        values.put("read", true);
                        Messola.getContext().getContentResolver().update(Uri.parse("content://sms/inbox"), values, "_id=" + SmsMessageId, null);
                        return;
                    }
                }
            }
        }catch(Exception e)
        {
            Log.e("Mark Read", "Error in Read: "+e.toString());
        }
    }

    private void defaultAppResolve() {

        if (!Utils.isDefaultSmsApp(getApplicationContext())) {
            Log.d(TAG, "not default app");
            // App is not default.
            // Show the "not currently set as the default SMS app" interface
            View viewGroup = findViewById(R.id.not_default_app);
            viewGroup.setVisibility(View.VISIBLE);

            // Set up a button that allows the user to change the default SMS app
            setUpDefaultAppResolver();
        } else {
            // App is the default.
            // Hide the "not currently set as the default SMS app" interface
            View viewGroup = findViewById(R.id.not_default_app);
            viewGroup.setVisibility(View.GONE);
        }
    }

    @TargetApi(19)
    private void setUpDefaultAppResolver(){
        Button button = (Button) findViewById(R.id.change_default_app);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
                startActivityForResult(intent, DEFAULT_SMS_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DEFAULT_SMS_REQUEST:
                int msgId;
                if (resultCode == Activity.RESULT_OK) {
                    View viewGroup = findViewById(R.id.not_default_app);
                    viewGroup.setVisibility(View.GONE);
                    msgId = R.string.sms_default_success;
                } else {
                    msgId = R.string.sms_default_fail;
                }
                Toast.makeText(getBaseContext(), msgId, Toast.LENGTH_SHORT).show();

        }
    }



    private final int DEFAULT_SMS_REQUEST = 14;

}
