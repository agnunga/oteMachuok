package com.ag.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.ag.R;
import com.ag.data.Contact;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ag.
 */

public class ContactAdapter extends SelectableAdapter<ContactAdapter.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter{

    private List<Contact> mArrayList;
    private Context mContext;
    private ContactAdapter.ViewHolder.ClickListener clickListener;

    public ContactAdapter (Context context, List<Contact> arrayList, ContactAdapter.ViewHolder.ClickListener clickListener) {
        this.mArrayList = arrayList;
        this.mContext = context;
        this.clickListener = clickListener;

    }

    // Create new views
    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_contact, null);
        ContactAdapter.ViewHolder viewHolder = new ContactAdapter.ViewHolder(itemLayoutView,clickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ContactAdapter.ViewHolder viewHolder, int position) {
        viewHolder.tvName.setText(mArrayList.get(position).getName());
        viewHolder.userPhoto.setImageBitmap(mArrayList.get(position).getImage());
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return String.valueOf(mArrayList.get(position).getName().charAt(0));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener,View.OnLongClickListener  {

        public TextView tvName;
        public ImageView userPhoto;
//        public TextView tvPhoneNo;
        private ContactAdapter.ViewHolder.ClickListener listener;
        //private final View selectedOverlay;


        public ViewHolder(View itemLayoutView,ContactAdapter.ViewHolder.ClickListener listener) {
            super(itemLayoutView);

            this.listener = listener;

            tvName = (TextView) itemLayoutView.findViewById(R.id.tv_user_name);
//            tvPhoneNo = (TextView) itemLayoutView.findViewById(R.id.tv_user_name);
            userPhoto = (ImageView) itemLayoutView.findViewById(R.id.iv_user_photo);

            itemLayoutView.setOnClickListener(this);

            itemLayoutView.setOnLongClickListener (this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onItemClicked(getAdapterPosition ());
            }
        }
        @Override
        public boolean onLongClick (View view) {
            if (listener != null) {
                return listener.onItemLongClicked(getAdapterPosition ());
            }
            return false;
        }

        public interface ClickListener {
            public void onItemClicked(int position);

            public boolean onItemLongClicked(int position);

            boolean onCreateOptionsMenu(Menu menu);
        }
    }
}
