package com.ag.recylcerconv;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ag.R;
import com.ag.data.Conversation;
import com.ag.utilis.DateUtil;
import java.util.Date;
import java.util.List;

/**
 * Created by Ag.
 */

public class ConversationRecyclerView extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // The conversationList to display in your RecyclerView
    private List<Conversation> conversationList;
    private Context mContext;

    private final int DATE = 0, YOU = 1, ME = 2;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ConversationRecyclerView(Context context, List<Conversation> conversationList) {
        this.mContext = context;
        this.conversationList = conversationList;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.conversationList.size();
    }

    @Override
    public int getItemViewType(int position) {
        //More to come
        if (conversationList.get(position).getType().equals("0")) {
            return DATE;
        } else if (conversationList.get(position).getType().equals("1")) {
            return YOU;
        }else if (conversationList.get(position).getType().equals("2")) {
            return ME;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case DATE:
                View v1 = inflater.inflate(R.layout.layout_holder_date, viewGroup, false);
                viewHolder = new HolderDate(v1);
                break;
            case YOU:
                View v2 = inflater.inflate(R.layout.layout_holder_you, viewGroup, false);
                viewHolder = new HolderYou(v2);
                break;
            default:
                View v = inflater.inflate(R.layout.layout_holder_me, viewGroup, false);
                viewHolder = new HolderMe(v);
                break;
        }
        return viewHolder;
    }
    public void addItem(List<Conversation> item) {
        conversationList.addAll(item);
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case DATE:
                HolderDate vh1 = (HolderDate) viewHolder;
                configureViewHolder1(vh1, position);
                break;
            case YOU:
                HolderYou vh2 = (HolderYou) viewHolder;
                configureViewHolder2(vh2, position);
                break;
            default:
                HolderMe vh = (HolderMe) viewHolder;
                configureViewHolder3(vh, position);
                break;
        }
    }

    private void configureViewHolder3(HolderMe vh1, int position) {
            vh1.getTime().setText(DateUtil.ago2(new Date(conversationList.get(position).getDate())));
            vh1.getChatText().setText(conversationList.get(position).getBody());
    }

    private void configureViewHolder2(HolderYou vh1, int position) {
            vh1.getTime().setText(DateUtil.ago2(new Date(conversationList.get(position).getDate())));
            vh1.getChatText().setText(conversationList.get(position).getBody());
    }
    private void configureViewHolder1(HolderDate vh1, int position) {
            vh1.getDate().setText(conversationList.get(position).getBody());
    }

}
