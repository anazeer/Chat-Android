package com.excilys.android.formation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.excilys.android.formation.R;
import com.excilys.android.formation.model.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for a message
 */
public class MessageAdapter extends ArrayAdapter<Message> {

    private TextView chatText;
    private List<Message> messageList = new ArrayList<>();
    private Context context;

    @Override
    public void add(Message object) {
        messageList.add(object);
        super.add(object);
    }

    public MessageAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    @Override
    public int getCount() {
        return this.messageList.size();
    }

    public void setChatList(List<Message> messageList) {
        int oldCount = this.messageList.size();
        int newCount = messageList.size();
        this.messageList = messageList;
        if (oldCount != newCount) {
            notifyDataSetChanged();
        }
    }

    @Override
    public Message getItem(int index) {
        return this.messageList.get(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message chat = getItem(position);
        LayoutInflater inflater = (LayoutInflater) this.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // The message is on the right if it has been send from the current user
        if (chat.isUser()) {
            convertView = inflater.inflate(R.layout.right_text, parent, false);
        } else {
            convertView = inflater.inflate(R.layout.left_text, parent, false);
        }
        chatText = (TextView) convertView.findViewById(R.id.msg);
        // Output the login before the message
        String msg = chat.getLogin() + "\n" + chat.getMessage();
        msg += "\n" + chat.getImage();
        chatText.setText(msg);
        return convertView;
    }
}