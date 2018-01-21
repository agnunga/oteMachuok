package com.ag;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ag.data.Contact;
import com.ag.data.Conversation;
import com.ag.data.Message;
import com.ag.data.MessageStore;
import com.ag.recylcerchat.ChatData;
import com.ag.recylcerchat.ConversationRecyclerView;

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
                    if (text.getText().length() > 0){
                        sendMessage(mConv, text.getText().toString());
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

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent i = new Intent(getContext(), MainActivity.class);
        startActivity(i);
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
