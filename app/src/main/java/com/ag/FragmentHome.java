package com.ag;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ag.data.Conversation;
import com.ag.data.ConversationStore;
import com.ag.recyclerview.Chat;
import com.ag.recyclerview.ChatAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ag.
 */

public class FragmentHome extends Fragment implements ChatAdapter.ViewHolder.ClickListener{
    private RecyclerView mRecyclerView;
    private ChatAdapter mAdapter;
    private TextView tv_selection;

    ConversationStore conversationStore;

    public FragmentHome(){
        setHasOptionsMenu(true);
    }
    public void onCreate(Bundle a){
        super.onCreate(a);
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null, false);

        conversationStore = new ConversationStore();

        getActivity().supportInvalidateOptionsMenu();
        ((MainActivity)getActivity()).changeTitle(R.id.toolbar, "Messages");

        tv_selection = (TextView) view.findViewById(R.id.tv_selection);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ChatAdapter(getContext(),setData(),this);
        mRecyclerView.setAdapter (mAdapter);

        return view;
    }
    public List<Chat> setData(){
        List<Conversation> conversations = conversationStore.getAllConversations();
        List<Chat> data = new ArrayList<>();

        System.out.println("=============================================="+conversations.size());

        //        @DrawableRes int img[]= {R.drawable.userpic , R.drawable.user1, R.drawable.user2, R.drawable.user3, R.drawable.user4 , R.drawable.userpic , R.drawable.user1, R.drawable.user2, R.drawable.user3, R.drawable.user4 , R.drawable.user1, R.drawable.user2, R.drawable.user3, R.drawable.user4 , R.drawable.userpic , R.drawable.user1, R.drawable.user2, R.drawable.user3, R.drawable.user4 , R.drawable.user1, R.drawable.user2, R.drawable.user3, R.drawable.user4 , R.drawable.userpic , R.drawable.user1, R.drawable.user2, R.drawable.user3, R.drawable.user4 , R.drawable.user1, R.drawable.user2, R.drawable.user3, R.drawable.user4 , R.drawable.userpic , R.drawable.user1, R.drawable.user2, R.drawable.user3, R.drawable.user4 , R.drawable.user1, R.drawable.user2, R.drawable.user3, R.drawable.user4 , R.drawable.userpic , R.drawable.user1, R.drawable.user2, R.drawable.user3, R.drawable.user4 , R.drawable.user1, R.drawable.user2, R.drawable.user3, R.drawable.user4 , R.drawable.userpic , R.drawable.user1, R.drawable.user2, R.drawable.user3, R.drawable.user4 };

        for (Conversation c: conversations) {
            Chat chat = new Chat();
            chat.setmTime("5:04pm");
            chat.setName(c.contact.name);
            chat.setImage(R.drawable.userpic);
            chat.setOnline(true);
            chat.setLastChat(c.lastMsgId +" bhjg ");
            data.add(chat);
        }
        return data;
    }

    @Override
    public void onItemClicked (int position) {
        startActivity(new Intent(getActivity(), ConversationActivity.class));
        Conversation c = null;
        if(position > 0) {
            c = conversationStore.getConversation(position);
        }
        startComposing(c);
    }

    private void startComposing(Conversation c) {
        Intent i = new Intent(getActivity(), ConversationActivity.class);
        if(c != null) {
            Log.d("SimpleSMS", c.toString());
            i.putExtra("thread_id", c.threadId);
            i.putExtra("name", c.contact.name);
            i.putExtra("number", c.contact.number);
            i.putExtra("contact_id", c.contact.id);
        }
        startActivity(i);
    }



    @Override
    public boolean onItemLongClicked (int position) {
        toggleSelection(position);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    private void toggleSelection(int position) {
        mAdapter.toggleSelection (position);
        if (mAdapter.getSelectedItemCount()>0){
            tv_selection.setVisibility(View.VISIBLE);
        }else
            tv_selection.setVisibility(View.GONE);

        getActivity().runOnUiThread(new Runnable() {
            public void run()
            {
                tv_selection.setText("Delete ("+mAdapter.getSelectedItemCount()+")");
            }
        });

    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_edit, menu);
    }
/*

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("OLOO", "\n\n\n\n===Selected X "+item.getItemId()+"===\n\n\n\n\n");

        switch (item.getItemId()) {
            case R.menu.menu_edit : {
                Log.i("OLOO", "\n\n\n\n===Create new messo===\n\n\n\n\n");
                Intent i = new Intent(getContext(), ComposeMessageActivity.class);
                startActivity(i);
                return true;
            }
        }
        return false;
    }
*/

}
