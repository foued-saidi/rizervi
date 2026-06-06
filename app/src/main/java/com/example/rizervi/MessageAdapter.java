package com.example.rizervi;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<ChatMessage> messages;
    private String currentUserId;

    public MessageAdapter(List<ChatMessage> messages, String currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessage msg = messages.get(position);
        holder.tvText.setText(msg.getMessage());

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.layoutContainer.getLayoutParams();
        if (msg.getSenderId().equals(currentUserId)) {
            params.gravity = Gravity.END;
            holder.layoutContainer.setBackgroundResource(R.drawable.bg_message_sent);
        } else {
            params.gravity = Gravity.START;
            holder.layoutContainer.setBackgroundResource(R.drawable.bg_message_received);
        }
        holder.layoutContainer.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvText;
        LinearLayout layoutContainer;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tvMessageText);
            layoutContainer = itemView.findViewById(R.id.layoutMessageContainer);
        }
    }
}
