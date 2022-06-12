package com.socialnet.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.socialnet.android.MainActivity;
import com.socialnet.android.R;
import com.socialnet.android.gson.ContactRecord;
import com.socialnet.android.gson.FriendItem;

import java.util.List;

public class RecordItemAdapter extends ArrayAdapter<ContactRecord> {
    private int resourceId;
    private MainActivity mainActivity;

    public RecordItemAdapter(@NonNull Context context, int resource, @NonNull List<ContactRecord> objects) {
        super(context, resource, objects);
        this.resourceId = resource;
        this.mainActivity = (MainActivity) context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ContactRecord item = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView date = view.findViewById(R.id.date_text);
        date.setText(item.getDate());

//        TextView from = view.findViewById(R.id.from_person);
//        from.setText(item.getFromPerson());

        TextView to = view.findViewById(R.id.to_person);
        to.setText(item.getToPerson());
        return view;
    }


}
