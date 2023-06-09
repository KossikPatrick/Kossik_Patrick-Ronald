package com.mindtoheart.licenta.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.mindtoheart.licenta.R;
//adapter between the Model (the ChatMessage objects) and the View (the UI components that display the chat messages)
public class ChatAdapter extends ArrayAdapter<ChatMessage> {
    private LayoutInflater inflater;

    public ChatAdapter(@NonNull Context context) {
        super(context, 0);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_chat_message, parent, false);
            holder = new ViewHolder();
            holder.senderTextView = convertView.findViewById(R.id.senderTextView);
            holder.messageTextView = convertView.findViewById(R.id.messageTextView);
            holder.timestampTextView = convertView.findViewById(R.id.timestampTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ChatMessage chatMessage = getItem(position);
        if (chatMessage != null) {
            holder.senderTextView.setText(chatMessage.getSender());
            holder.messageTextView.setText(chatMessage.getMessage());
            holder.timestampTextView.setText(chatMessage.getTimestamp());
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView senderTextView;
        TextView messageTextView;
        TextView timestampTextView;
    }
}