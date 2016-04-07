package com.excilys.formation.exos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.excilys.formation.exos.model.Message;
import com.excilys.formation.exos.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for
 */
public class MessageArrayAdapter extends ArrayAdapter<Message> {

    private TextView chatText;
    private List<Message> messageList = new ArrayList<>();
    private Context context;

    @Override
    public void add(Message object) {
        messageList.add(object);
        super.add(object);
    }

    public MessageArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    @Override
    public int getCount() {
        return this.messageList.size();
    }

    public void setChatList(List<Message> messageList) {
        this.messageList = messageList;
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
        if (chat.isUser()) {
            convertView = inflater.inflate(R.layout.right_text, parent, false);
        } else {
            convertView = inflater.inflate(R.layout.left_text, parent, false);
        }
        chatText = (TextView) convertView.findViewById(R.id.msg);
        String msg = chat.getLogin() + "\n" + chat.getMessage();
        chatText.setText(msg);
        return convertView;
    }
}