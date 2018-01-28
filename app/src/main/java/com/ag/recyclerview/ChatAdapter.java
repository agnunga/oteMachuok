package com.ag.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.ag.R;
import com.ag.data.Chat;
import com.ag.utilis.CommonUtil;
import com.ag.utilis.DateUtil;

import java.util.Date;
import java.util.List;

/**
 * Created by Ag.
 */

public class ChatAdapter extends SelectableAdapter<ChatAdapter.ViewHolder> {

    private List<Chat> chatList;
    private Context mContext;
    private ViewHolder.ClickListener clickListener;

    public ChatAdapter (Context context, List<Chat> arrayList, ViewHolder.ClickListener clickListener) {
        this.chatList = arrayList;
        this.mContext = context;
        this.clickListener = clickListener;

    }

    // Create new views
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item_chat, null);

        ViewHolder viewHolder = new ViewHolder(itemLayoutView,clickListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        viewHolder.tvName.setText(chatList.get(position).getContact().getName());
        if (isSelected(position)) {
            viewHolder.checked.setChecked(true);
            viewHolder.checked.setVisibility(View.VISIBLE);
        }else{
            viewHolder.checked.setChecked(false);
            viewHolder.checked.setVisibility(View.GONE);
        }
        viewHolder.tvTime.setText(DateUtil.ago(new Date(chatList.get(position).getDate())));
        viewHolder.userPhoto.setImageResource(chatList.get(position).getContact().getImage());
        if (chatList.get(position).isOnline()){
            viewHolder.onlineView.setVisibility(View.VISIBLE);
        }else
            viewHolder.onlineView.setVisibility(View.INVISIBLE);

        viewHolder.tvLastChat.setText(chatList.get(position).getSnippet());
        viewHolder.tvLastChat.setText(chatList.get(position).getSnippet());
//        viewHolder.tvCount.setText(CommonUtil.cutCount(chatList.get(position).getMsgCount()));

        if(chatList.get(position).getRead() == 1){
            viewHolder.tvName.setTextColor(mContext.getResources().getColor(R.color.colorTextBlack));
        }else {
            viewHolder.tvName.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        }

        long unreadCount  = chatList.get(position).getUnreadCount();
        if (unreadCount > 0) {
            viewHolder.tvCount.setText(CommonUtil.cutCount(unreadCount));
        }else {
            viewHolder.tvCount.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener,View.OnLongClickListener  {

        public TextView tvName;
        public TextView tvTime;
        public TextView tvLastChat;
        public TextView tvCount;
        public ImageView userPhoto;
        public boolean online = false;
        private final View onlineView;
        public CheckBox checked;
        private ClickListener listener;
        //private final View selectedOverlay;

        public ViewHolder(View itemLayoutView,ClickListener listener) {
            super(itemLayoutView);

            this.listener = listener;

            tvName = (TextView) itemLayoutView.findViewById(R.id.tv_user_name);
            //selectedOverlay = (View) itemView.findViewById(R.id.selected_overlay);
            tvTime = (TextView) itemLayoutView.findViewById(R.id.tv_time);
            tvLastChat = (TextView) itemLayoutView.findViewById(R.id.tv_last_chat);
            tvCount = (TextView) itemLayoutView.findViewById(R.id.tv_unread_count);
            userPhoto = (ImageView) itemLayoutView.findViewById(R.id.iv_user_photo);
            onlineView = (View) itemLayoutView.findViewById(R.id.online_indicator);
            checked = (CheckBox) itemLayoutView.findViewById(R.id.chk_list);

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
