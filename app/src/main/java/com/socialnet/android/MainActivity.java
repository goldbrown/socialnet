package com.socialnet.android;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.socialnet.android.adapter.FriendItemAdapter;
import com.socialnet.android.adapter.RecordItemAdapter;
import com.socialnet.android.gson.ContactInfo;
import com.socialnet.android.gson.ContactRecord;
import com.socialnet.android.gson.FriendInfo;
import com.socialnet.android.gson.FriendItem;
import com.socialnet.android.gson.OneSentenceResponse;
import com.socialnet.android.util.HttpUtil;
import com.socialnet.android.util.Utility;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    private ImageView bingPicImg;
    private ScrollView socialNetLayout;
    private TextView pageTitle;

    private LinearLayout recordArea;
    private TextView dateText;
//    private TextView fromPerson;
    private TextView toPerson;

    private LinearLayout friendArea;

    private TextView oneSentenceText;
    private TextView friendName;
//    private TextView friendTag;
    private TextView contactCount;

    private ListView friendListView;
    private List<FriendItem> friendItemList = new LinkedList<>();
    private FriendItemAdapter friendItemAdapter;

    private ListView recordListView;
    private List<ContactRecord> contactRecordList = new LinkedList<>();
    private RecordItemAdapter recordItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_main);

        bingPicImg = findViewById(R.id.bing_pic_img);
//        socialNetLayout = findViewById(R.id.socialnet_layout);
        pageTitle = findViewById(R.id.page_title);

        // 记录表
//        recordArea =findViewById(R.id.record_area);
        dateText = findViewById(R.id.date_text);
//        fromPerson = findViewById(R.id.from_person);
        toPerson = findViewById(R.id.to_person);

        // 朋友表
//        friendArea = findViewById(R.id.friend_area);
        friendName = findViewById(R.id.friend_name);
//        friendTag = findViewById(R.id.friend_tag);
        contactCount = findViewById(R.id.contact_count);

        // 每日一句
        oneSentenceText = findViewById(R.id.one_sentence_text);

        pageTitle.setText("情感账户");
        // get contact records
        // 背景图
        loadBingPic();

        fillContactInfo();
        // 设置每日一句
        requestOneSentence();

    }

    private void fillContactInfo() {
        // 联系记录
        recordListView = findViewById(R.id.record_list);
        ContactInfo contactInfo = getContactInfoFromDb(10);
        if (contactInfo != null) {
            contactRecordList = contactInfo.getContactRecords();
            recordItemAdapter = new RecordItemAdapter(MainActivity.this, R.layout.record_item, contactRecordList);

            View header = LayoutInflater.from(this).inflate(R.layout.record_list_header, recordListView, false);
            recordListView.addHeaderView(header, null, false);
            recordListView.setAdapter(recordItemAdapter);
            fixListViewHeight(recordListView);
        }

        // 朋友列表
        friendListView = findViewById(R.id.friend_list);
        friendItemList = calFriendDataList(contactInfo.getContactRecords());
        if (CollectionUtil.isNotEmpty(friendItemList)) {
            friendItemAdapter = new FriendItemAdapter(MainActivity.this, R.layout.friend_item, friendItemList);
            View header = LayoutInflater.from(this).inflate(R.layout.friend_list_header, friendListView, false);
            friendListView.addHeaderView(header, null, false);
            friendListView.setAdapter(friendItemAdapter);
            fixListViewHeight(friendListView);
        }

    }

    private void fixListViewHeight(ListView listView) {
        ListAdapter adapter = listView.getAdapter();
        int totalHeight = 0;
        if (adapter == null || adapter.getCount() <= 0) {
            return;
        }
        for (int i = 0; i < adapter.getCount(); i++) {
            View item = adapter.getView(i, null, listView);
            item.measure(0, 0);
            totalHeight += item.getMeasuredHeight();
        }
        ViewGroup.LayoutParams layoutParams = listView.getLayoutParams();
        layoutParams.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(layoutParams);
    }

    private List<FriendItem> calFriendDataList(List<ContactRecord> contactRecords) {
        List<FriendItem> list = new LinkedList<>();
        if (CollectionUtil.isNotEmpty(contactRecords)) {
            Map<String, Long> person2CountMap = contactRecords.stream().filter(Objects::nonNull)
                    .collect(Collectors.groupingBy(item -> item.getToPerson(), Collectors.counting()));
            for (Map.Entry<String, Long> entry : person2CountMap.entrySet()) {
                int countInt = Math.toIntExact(entry.getValue());
                list.add(new FriendItem.Builder().friendName(entry.getKey()).countInt(countInt).count(String.valueOf(countInt)).build());
            }
            list = list.stream().filter(Objects::nonNull).sorted(Comparator.comparing(FriendItem::getCountInt).reversed())
                    .collect(Collectors.toCollection(LinkedList::new));
        }
        return list;
    }

    private ContactInfo getContactInfoFromDb(int limit) {


        ContactInfo contactInfo = new ContactInfo();
        List<ContactRecord> records = new LinkedList<>();

        records.add(new ContactRecord.Builder().toPerson("lisi")
                .date("2022-06-12 12:12:22").build());
        records.add(new ContactRecord.Builder().toPerson("zhangsan")
                .date("2022-06-11 12:12:22").build());
        records.add(new ContactRecord.Builder().toPerson("wangwu")
                .date("2022-06-11 12:12:22").build());
        records.add(new ContactRecord.Builder().toPerson("wangwu")
                .date("2022-06-12 12:12:22").build());
        contactInfo.setContactRecords(records);
        return contactInfo;
    }

    public void addRecordItem(ContactRecord contactRecord) {
        LinkedList<ContactRecord> contactRecordList = (LinkedList<ContactRecord>) this.contactRecordList;
        contactRecordList.addFirst(contactRecord);

        this.friendItemList.clear();
        this.friendItemList.addAll(calFriendDataList(contactRecordList));

        // notify refresh view
        friendListView = findViewById(R.id.friend_list);
        if (friendListView.getAdapter() instanceof HeaderViewListAdapter) {
            HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) friendListView.getAdapter();
            friendItemAdapter = (FriendItemAdapter) headerViewListAdapter.getWrappedAdapter();
            fixListViewHeight(this.friendListView);
            friendItemAdapter.notifyDataSetChanged();
        }

        recordListView = findViewById(R.id.record_list);
        if (recordListView.getAdapter() instanceof HeaderViewListAdapter) {
            HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) recordListView.getAdapter();
            recordItemAdapter = (RecordItemAdapter) headerViewListAdapter.getWrappedAdapter();
            fixListViewHeight(this.recordListView);
            recordItemAdapter.notifyDataSetChanged();
        }
    }


    public void minusRecordItem(String friendName) {
        Iterator<ContactRecord> iterator = contactRecordList.iterator();
        while (iterator.hasNext()) {
            ContactRecord next = iterator.next();
            if (next.getToPerson().equals(friendName)) {
                iterator.remove();
                break;
            }
        }
        this.friendItemList.clear();
        this.friendItemList = calFriendDataList(contactRecordList);

        // notify refresh view
        friendListView = findViewById(R.id.friend_list);
        if (friendListView.getAdapter() instanceof HeaderViewListAdapter) {
            HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) friendListView.getAdapter();
            friendItemAdapter = (FriendItemAdapter) headerViewListAdapter.getWrappedAdapter();
            fixListViewHeight(this.friendListView);
            friendItemAdapter.notifyDataSetChanged();
        }

        recordListView = findViewById(R.id.record_list);
        if (recordListView.getAdapter() instanceof HeaderViewListAdapter) {
            HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) recordListView.getAdapter();
            recordItemAdapter = (RecordItemAdapter) headerViewListAdapter.getWrappedAdapter();
            fixListViewHeight(this.recordListView);
            recordItemAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 请求每日一句
     */
    public void requestOneSentence() {
        String weatherUrl = "https://apiv3.shanbay.com/weapps/dailyquote/quote/";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final OneSentenceResponse oneSentenceResponse = Utility.convert2OneSentenceResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (oneSentenceResponse != null && StrUtil.isNotBlank(oneSentenceResponse.getContent())) {
                            String sentence = oneSentenceResponse.getContent();
                            oneSentenceText.setText(sentence);
                        } else {
                            Toast.makeText(MainActivity.this, "获取名句失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "获取名句失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 加载必应每日一图
     */
    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
//                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
//                editor.putString("bing_pic", bingPic);
//                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(MainActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

}