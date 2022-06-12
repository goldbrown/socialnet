package com.socialnet.android.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.contextaware.ContextAware;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.socialnet.android.MainActivity;
import com.socialnet.android.R;
import com.socialnet.android.gson.ContactRecord;
import com.socialnet.android.gson.FriendItem;

import java.util.ArrayDeque;
import java.util.Date;
import java.util.List;

import cn.hutool.core.date.format.FastDateFormat;

public class FriendItemAdapter extends ArrayAdapter<FriendItem> implements View.OnClickListener {
    private int resourceId;
    private List<FriendItem> friendItemList;
    private MainActivity mainActivity;


    private static FastDateFormat fastDateFormat = FastDateFormat.getInstance("yyyy-MM-dd hh:mm:ss");

    public FriendItemAdapter(@NonNull Context context, int resource, @NonNull List<FriendItem> objects) {
        super(context, resource, objects);
        this.resourceId = resource;
        this.friendItemList = objects;
        this.mainActivity = (MainActivity) context;
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

//        TextView tag = view.findViewById(R.id.friend_tag);
//        tag.setText(item.getFriendTag());

        Button incrBtn = view.findViewById(R.id.incr_contact);
        incrBtn.setTag(R.id.incr_contact, position);
        incrBtn.setOnClickListener(this);

        Button decrBtn = view.findViewById(R.id.decr_contact);
        decrBtn.setTag(R.id.decr_contact, position);
        decrBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        FriendItem item;
        View view;
        if (v.getId() == R.id.incr_contact) {
            item = getItem((Integer) v.getTag(R.id.incr_contact));

//                Toast.makeText(v.getContext(), v.getId() + " clicked", Toast.LENGTH_SHORT);
            view = LayoutInflater.from(getContext()).inflate(R.layout.friend_item, null);
            Log.i("TAG", "incr, friend name is : " + item.getFriendName());
            FriendItem friendItem = friendItemList.get((Integer) v.getTag(R.id.incr_contact));
            ContactRecord contactRecord = new ContactRecord();
            contactRecord.setToPerson(friendItem.getFriendName());
            contactRecord.setDate(fastDateFormat.format(new Date()));
            mainActivity.addRecordItem(contactRecord);
        } else if (v.getId() == R.id.decr_contact) {
            item = getItem((Integer) v.getTag(R.id.decr_contact));
//                Toast.makeText(v.getContext(), v.getId() + " clicked", Toast.LENGTH_SHORT);
            view = LayoutInflater.from(getContext()).inflate(R.layout.friend_item, null);
            Log.i("TAG", "decr, friend name is : " + item.getFriendName());
            FriendItem friendItem = friendItemList.get((Integer) v.getTag(R.id.decr_contact));
            mainActivity.minusRecordItem(friendItem.getFriendName());
        }

    }
}
