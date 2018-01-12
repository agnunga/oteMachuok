package com.uiresource.messenger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.uiresource.messenger.data.Contact;
import com.uiresource.messenger.data.Conversation;
import com.uiresource.messenger.data.Message;
import com.uiresource.messenger.data.MessageStore;
import com.uiresource.messenger.recyclerview.Chat;
import com.uiresource.messenger.recylcerchat.ChatData;
import com.uiresource.messenger.recylcerchat.ConversationRecyclerView;

import java.util.ArrayList;
import java.util.List;


public class ConversationActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private ConversationRecyclerView mAdapter;
    private EditText text;
    private Button send;
    private MessageStore mStore;
    private Conversation mConv;
    private ArrayList<Message> mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItems = new ArrayList<>();
        setContentView(R.layout.activity_conversation);

        Intent i = getIntent();
        if(i.hasExtra("thread_id")) {
            long threadId = i.getLongExtra("thread_id", -1);
            long contactId = i.getLongExtra("contact_id", -1);
            String number = i.getStringExtra("number");
            String name = i.getStringExtra("name");

            Contact c = new Contact(contactId, name, number);
            mConv = new Conversation();
            mConv.contact = c;
            mConv.threadId = threadId;

            mStore = new MessageStore(mConv.threadId, mConv.contact.name);
//            mStore.bindView(mHistory);
            mStore.update();
            setupToolbarWithUpNav(R.id.toolbar, mConv.contact.name, R.drawable.ic_action_back);
            System.out.println("SIZE mItems =============== " + mItems.size());
            mItems = mStore.getAllMessage();
            System.out.println("SIZE mItems =============== " + mItems.size());

            mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mAdapter = new ConversationRecyclerView(this,setData());
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
                }
            }, 500);

            text = (EditText) findViewById(R.id.et_message);
            text.setOnClickListener(new View.OnClickListener() {
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
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!text.getText().equals("")){
                        List<ChatData> data = new ArrayList<ChatData>();
                        ChatData item = new ChatData();
                        item.setTime("6:00pm");
                        item.setType("2");
                        item.setText(text.getText().toString());
                        data.add(item);
                        mAdapter.addItem(data);
                        mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() -1);
                        text.setText("");
                    }
                }
            });
        }
    }

    public List<ChatData> setData(){
        List<ChatData> data = new ArrayList<>();

        for (Message m: mItems){
            ChatData item = new ChatData();
            item.setType(m.type+"");
            item.setText(m.body);
            item.setTime("4:30pm");
            data.add(item);
        }
        return data;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_userphoto, menu);
        return true;
    }
}
