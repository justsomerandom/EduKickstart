package com.example.edukickstart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.example.edukickstart.ui.login.LoginActivity;

import java.util.List;

public class UserListAdapter extends BaseAdapter {

    private final Context context;
    private final List<User> userList;

    public UserListAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_user, parent, false);
        }

        Button userButton = convertView.findViewById(R.id.user_button);
        User user = userList.get(position);
        userButton.setText(user.getUsername());

        userButton.setOnClickListener(v -> {
            // Get the selected user
            User selectedUser = userList.get(position);
            // Set the username in the usernameEditText
            ((LoginActivity) context).setUsername(selectedUser.getUsername());
        });

        return convertView;
    }
}


