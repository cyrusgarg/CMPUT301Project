package com.example.myapplication.model;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;

import java.util.ArrayList;

public class UserListAdapter extends ArrayAdapter<Bundle> {

    private ArrayList<Bundle> users;
    private Context context;

    public UserListAdapter(Context context, ArrayList<Bundle> users){
        super(context,0,users);
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.users_list_content, parent, false);
        }

        Bundle user = users.get(position);

        TextView name = view.findViewById(R.id.user_name);

        TextView status = view.findViewById(R.id.user_status);

        name.setText(user.getString("name"));

        status.setText(user.getString("status"));
        //Log.d("Kenny", event.getEventName());
        return view;
    }
}
