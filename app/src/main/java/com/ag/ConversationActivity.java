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
    private EditText text;
    private Button send;
    private ConvStore convStore;
    private Chat chatItem;
    private ArrayList<Conv> convItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        convItems = new ArrayList<>();
        setContentView(R.layout.activity_conversation);

        Intent i = getIntent();
        if(i.hasExtra("thread_id")) {
            long threadId = i.getLongExtra("thread_id", -1);
            long contactId = i.getLongExtra("contact_id", -1);
            String number = i.getStringExtra("number");
            String name = i.getStringExtra("name");

            Contact c = new Contact(contactId, name, number, R.drawable.userpic);
            chatItem = new Chat();
            chatItem.setContact(c);
            chatItem.setThreadId(threadId);

            convStore = new ConvStore(chatItem.getThreadId());
             convStore.update();
            setupToolbarWithUpNav(R.id.toolbar, chatItem.getContact().getName(), R.drawable.ic_action_back);
//            System.out.println("SIZE convItems =============== " + convItems.size());
            convItems = convStore.getAllMessage();
            System.out.println("SIZE convItems =============== " + convItems.size());

            mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mAdapter = new ConversationRecyclerView(this, convItems);
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
                        sendMessage(chatItem, text.getText().toString());
                        List<Conv> data = new ArrayList<>();
                        Conv item = new Conv();
                        item.setDate(new Date().getTime());
                        item.setType("2");
                        item.setBody(text.getText().toString());
                        data.add(item);
                        mAdapter.addItem(data);
                        mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() -1);
                        text.setText("");
                    }
                }
            });
        }else {
            Log.i(TAG, "Has no thread id");
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent i = new Intent(Messola.getContext(), MainActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_userphoto, menu);
        return true;
    }
}
