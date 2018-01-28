package com.ag;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ag.data.Chat;
import com.ag.data.ChatStore;
import com.ag.recyclerview.ChatAdapter;

import java.util.List;

/**
 * Created by Ag.
 */

public class FragmentHome extends Fragment implements ChatAdapter.ViewHolder.ClickListener{
    private RecyclerView mRecyclerView;
    private ChatAdapter mAdapter;
    private TextView tv_selection;

    private ChatStore chatStore;

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

        chatStore = new ChatStore();
        List<Chat> chats = chatStore.getAllChats();

        getActivity().supportInvalidateOptionsMenu();
        ((MainActivity)getActivity()).changeTitle(R.id.toolbar, "Messages");

        tv_selection = (TextView) view.findViewById(R.id.tv_selection);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ChatAdapter(getContext(), chats,this);
        mRecyclerView.setAdapter (mAdapter);

        return view;
    }

    @Override
    public void onItemClicked (int position) {
        startActivity(new Intent(getActivity(), ConversationActivity.class));
        startComposing(chatStore.getConversation(position));
    }

    private void startComposing(Chat c) {
        Intent i = new Intent(getActivity(), ConversationActivity.class);
        if(c != null) {
            //Log.d("SimpleSMS", c.toString());
            i.putExtra("thread_id", c.getThreadId());
            i.putExtra("name", c.getContact().getName());
            i.putExtra("number", c.getContact().getNumber());
            i.putExtra("contact_id", c.getContact().getId());
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

}
