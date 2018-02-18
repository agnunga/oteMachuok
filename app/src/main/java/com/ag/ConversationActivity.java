package com.ag;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
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
import com.ag.data.ContactStore;
import com.ag.data.Conv;
import com.ag.data.Contact;
import com.ag.data.Chat;
import com.ag.data.ConvStore;
import com.ag.recylcerconv.ConversationRecyclerView;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConversationActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private ConversationRecyclerView mAdapter;
    private EditText newSmsEt;
    private Button send;
    private List<Conv> convItems;
    private ListView mHistory;
    private TextView mSubject;
    private TextView mTextEditor;
    private Button mSendButton;
    private ConvStore convStore;
    private Chat chatItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        convItems = new ArrayList<>();
        setContentView(R.layout.activity_conversation);

        Intent i = getIntent();
        //i.FLAG_ACTIVITY_REORDER_TO_FRONT;
        Boolean hasConvId = i.hasExtra("thread_id");
        Boolean hasContactId = i.hasExtra("contact_id");
        if(hasConvId || hasContactId) {
            long threadId = i.getLongExtra("thread_id", -1);
            long contactId = i.getLongExtra("contact_id", -1);
            String number = i.getStringExtra("number");
            String name = i.getStringExtra("name");

            Contact c = new Contact(contactId, name, number, R.drawable.userpic);
            chatItem = new Chat();
            chatItem.setContact(c);
            chatItem.setThreadId(threadId);
            //System.out.println("CHAT ITEM ::::::::::: " + chatItem.toString());
            if (hasConvId) {
                convStore = new ConvStore(threadId);
            }
            else {
                convStore = new ConvStore(number);
            }

            //convStore.update();
            setupToolbarWithUpNav(R.id.toolbar, chatItem.getContact().getName(), R.drawable.ic_action_back);
            convItems = convStore.getAllConvs();
            System.out.println("SIZE convItems =============== " + convItems.size());
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(linearLayoutManager);
            //linearLayoutManager.setReverseLayout(true);
            mAdapter = new ConversationRecyclerView(this, convItems);
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

            final String final_number = number;
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (newSmsEt.getText().length() > 0){
                        //Send and save sms
                        /*selectSim(final_number);*/
                        Messola.sendMessage2(final_number, newSmsEt.getText().toString());
                        List<Conv> convList = new ArrayList<>();
                        Conv convItem = new Conv();
                        convItem.setDate(new Date().getTime());
                        convItem.setType("2");
                        convItem.setBody(newSmsEt.getText().toString());
                        convList.add(convItem);
                        mAdapter.addItem(convList);
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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
       /* Intent i = new Intent(this, MainActivity.class);
        startActivity(i);*/
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
                Contact c = ContactStore.getByNumber(mSubject.getText().toString());
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
    }
}
