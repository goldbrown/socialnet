package com.socialnet.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.socialnet.android.R;
import com.socialnet.android.gson.FriendItem;

import java.util.List;

public class FriendItemAdapter extends ArrayAdapter<FriendItem> {
    private int resourceId;

    public FriendItemAdapter(@NonNull Context context, int resource, @NonNull List<FriendItem> objects) {
        super(context, resource, objects);
        this.resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        FriendItem item = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView friendName = view.findViewById(R.id.friend_name);
        friendName.setText(item.getFriendName());

        TextView contactCount = view.findViewById(R.id.contact_count);
        contactCount.setText(item.getCount());

        TextView tag = view.findViewById(R.id.friend_tag);
        tag.setText(item.getFriendTag());
        return view;
    }
}
