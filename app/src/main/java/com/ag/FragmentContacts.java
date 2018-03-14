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
import com.ag.data.Contact;
import com.ag.data.ContactStore;
import com.ag.recyclerview.ContactAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Ag.
 */

public class FragmentContacts extends Fragment implements ContactAdapter.ViewHolder.ClickListener{
    private RecyclerView mRecyclerView;
    private ContactAdapter mAdapter;
    private List<Contact> contacts;
    private ContactStore contactStore;

    public FragmentContacts(){
        setHasOptionsMenu(true);
    }

    public void setList(List<Contact> list) {
        this.contacts = list;
    }

    public void onCreate(Bundle a){
        super.onCreate(a);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, null, false);
        Log.i(Messola.TAG, "All contacts");

        contactStore = new ContactStore();
        contacts = contactStore.getContactList();
//        new ContactStore(this).execute();

        getActivity().supportInvalidateOptionsMenu();
        ((MainActivity)getActivity()).changeTitle(R.id.toolbar, "Contacts");

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //linearLayoutManager.setReverseLayout(true);
        mAdapter = new ContactAdapter(getContext(), contacts,this);
        mRecyclerView.setAdapter (mAdapter);

        return view;
    }

    @Override
    public void onItemClicked (int position) {
        Contact contact = contacts.get(position);
        Log.i(Messola.TAG, "Contact ::: " + contact.toString());
        sctartConversationActivity(contact);
    }

    public void sctartConversationActivity (Contact c) {
        Intent i = new Intent(getActivity(), ConversationActivity.class);
        if(c != null) {
            //Log.d("SimpleSMS", c.toString());
            Set<String> numbers = contactStore.findNumbersById(c.getId());
            Iterator<String> iterator = numbers.iterator();
            while (iterator.hasNext()){
                i.putExtra("number", iterator.next());
            }
            i.putExtra("name", c.getName());
            i.putExtra("contact_id", c.getId());
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
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_add, menu);
    }

}
