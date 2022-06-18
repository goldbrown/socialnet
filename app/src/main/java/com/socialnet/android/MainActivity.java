package com.socialnet.android;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.socialnet.android.db.ContactRecordEntity;
import com.socialnet.android.db.FriendEntity;
import com.socialnet.android.gson.ContactRecord;
import com.socialnet.android.gson.FriendItem;
import com.socialnet.android.gson.OneSentenceResponse;
import com.socialnet.android.util.HttpUtil;
import com.socialnet.android.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.format.FastDateFormat;
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
//    private List<FriendEntity> friendEntities = new ArrayList<>();
//    private List<ContactRecordEntity> contactRecordEntities = new ArrayList<>();

    private FastDateFormat fastDateFormat = FastDateFormat.getInstance("yyyy-MM-dd hh:mm:ss");

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

        // 从DB恢复数据
        contactRecordList = getContactInfoFromDb(10);
        friendItemList = getFriendsFromDb();
        fillContactInfo();
        // 设置每日一句
        requestOneSentence();

    }

    private List<FriendItem> getFriendsFromDb() {
        List<FriendItem> res = new ArrayList<>();
        List<FriendEntity> friendEntities = DataSupport.findAll(FriendEntity.class);
        if (CollectionUtil.isNotEmpty(friendEntities)) {
            res = friendEntities.stream().map(item -> {
                FriendItem friendItem = new FriendItem();
                friendItem.setFriendName(item.getFriendName());
                friendItem.setCount("0");
                friendItem.setCountInt(0);
                return friendItem;
            }).collect(Collectors.toList());
        }
        return res;
    }

    private List<ContactRecord> mockData() {
        List<ContactRecordEntity> res = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ContactRecordEntity contactRecordEntity = new ContactRecordEntity();
            contactRecordEntity.setDateStr(fastDateFormat.format(new Date()));
            contactRecordEntity.setFriendName("zhangsan" + i);
            res.add(contactRecordEntity);
        }
        return res.stream().map(item -> {
            ContactRecord contactRecord = new ContactRecord();
            contactRecord.setToPerson(item.getFriendName());
            contactRecord.setDateStr(item.getDateStr());
            try {
                contactRecord.setDate(fastDateFormat.parse(item.getDateStr()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return contactRecord;
        }).collect(Collectors.toList());
    }

    private void fillContactInfo() {
        // 联系记录
        recordListView = findViewById(R.id.record_list);
        if (contactRecordList != null) {
            recordItemAdapter = new RecordItemAdapter(MainActivity.this, R.layout.record_item, contactRecordList);
            View header = LayoutInflater.from(this).inflate(R.layout.record_list_header, recordListView, false);
            recordListView.addHeaderView(header, null, false);
            recordListView.setAdapter(recordItemAdapter);
            fixListViewHeight(recordListView, contactRecordList.size());
        }

        // 朋友列表
        friendListView = findViewById(R.id.friend_list);
        friendItemList = calFriendCountList();
        if (CollectionUtil.isNotEmpty(friendItemList)) {
            friendItemAdapter = new FriendItemAdapter(MainActivity.this, R.layout.friend_item, friendItemList);
            View header = LayoutInflater.from(this).inflate(R.layout.friend_list_header, friendListView, false);
            friendListView.addHeaderView(header, null, false);
            friendListView.setAdapter(friendItemAdapter);
            fixListViewHeight(friendListView, friendItemList.size());
        }

    }

    private void fixListViewHeight(ListView listView, int size) {
        ListAdapter adapter = listView.getAdapter();
        int totalHeight = 0;
        if (size <= 0) {
            return;
        }
        for (int i = 0; i < size; i++) {
            if (listView.getAdapter() instanceof HeaderViewListAdapter) {
                HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) listView.getAdapter();
                ListAdapter listAdapter = headerViewListAdapter.getWrappedAdapter();
                View item = listAdapter.getView(i, null, listView);
                item.measure(0, 0);
                totalHeight += item.getMeasuredHeight();
            } else {
                View item = adapter.getView(i, null, listView);
                item.measure(0, 0);
                totalHeight += item.getMeasuredHeight();
            }
        }
        ViewGroup.LayoutParams layoutParams = listView.getLayoutParams();
        layoutParams.height = totalHeight + (listView.getDividerHeight() * (size)) + 20;
        listView.setLayoutParams(layoutParams);
    }

    private List<FriendItem> calFriendCountList() {
        Map<String, Long> mergedMap = new HashMap<>();
        List<FriendItem> list = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(friendItemList)) {
            for (FriendItem friendItem : friendItemList) {
                mergedMap.put(friendItem.getFriendName(), 0L);
            }
        }
        if (CollectionUtil.isNotEmpty(contactRecordList)) {
            Map<String, Long> person2CountMap = contactRecordList.stream().filter(Objects::nonNull)
                    .collect(Collectors.groupingBy(item -> item.getToPerson(), Collectors.counting()));
            mergedMap = mergeFriendMap(mergedMap, person2CountMap);
        }
        if (CollectionUtil.isNotEmpty(mergedMap)) {
            for (Map.Entry<String, Long> entry : mergedMap.entrySet()) {
                int countInt = Math.toIntExact(entry.getValue());
                list.add(new FriendItem.Builder().friendName(entry.getKey()).countInt(countInt).count(String.valueOf(countInt)).build());
            }
        }
        // sort
        list = list.stream().filter(Objects::nonNull).sorted(Comparator.comparing(FriendItem::getCountInt).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
        return list;
    }

    private Map<String, Long> mergeFriendMap(Map<String, Long> friend2CountMap, Map<String, Long> person2CountMap) {
        Map<String, Long> res = new HashMap<>(friend2CountMap);
        for (Map.Entry<String, Long> entry : person2CountMap.entrySet()) {
            if (res.containsKey(entry.getKey())) {
                String name = entry.getKey();
                res.put(name, res.get(name) + entry.getValue());
            } else {
                String name = entry.getKey();
                res.put(name, entry.getValue());
            }
        }
        return res;
    }

    private List<ContactRecord> getContactInfoFromDb(int limit) {
        List<ContactRecordEntity> contactRecordEntities = DataSupport.findAll(ContactRecordEntity.class);
        if (CollectionUtil.isEmpty(contactRecordEntities)) {
            List<ContactRecord> records = mockData();
            return records;
        }
        List<ContactRecord> records = new LinkedList<>();
        if (CollectionUtil.isNotEmpty(contactRecordEntities)) {
            for (ContactRecordEntity contactRecordEntity : contactRecordEntities) {
                try {
                    records.add(new ContactRecord.Builder().dateStr(contactRecordEntity.getDateStr())
                            .date(fastDateFormat.parse(contactRecordEntity.getDateStr()))
                            .toPerson(contactRecordEntity.getFriendName()).build());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return records;
    }

    public void addRecordItem(ContactRecord contactRecord) {
        this.contactRecordList.add(contactRecord);
        this.contactRecordList.sort(Comparator.comparing(ContactRecord::getDate).reversed());

        this.friendItemList = calFriendCountList();

        // notify refresh view
        friendListView = findViewById(R.id.friend_list);
        if (friendListView.getAdapter() instanceof HeaderViewListAdapter) {
            HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) friendListView.getAdapter();
            friendItemAdapter = new FriendItemAdapter(MainActivity.this, R.layout.friend_item, friendItemList);
            friendListView.setAdapter(friendItemAdapter);
            fixListViewHeight(this.friendListView, friendItemList.size());
        }

        recordListView = findViewById(R.id.record_list);
        if (recordListView.getAdapter() instanceof HeaderViewListAdapter) {
            HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) recordListView.getAdapter();
            recordItemAdapter = (RecordItemAdapter) headerViewListAdapter.getWrappedAdapter();
            fixListViewHeight(this.recordListView, contactRecordList.size());
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

        // notify refresh view
        this.friendItemList = calFriendCountList();
        friendListView = findViewById(R.id.friend_list);
        if (friendListView.getAdapter() instanceof HeaderViewListAdapter) {
//            HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) friendListView.getAdapter();
//            friendItemAdapter = (FriendItemAdapter) headerViewListAdapter.getWrappedAdapter();
            friendItemAdapter = new FriendItemAdapter(MainActivity.this, R.layout.friend_item, friendItemList);
            friendListView.setAdapter(friendItemAdapter);
            fixListViewHeight(this.friendListView, friendItemList.size());
        }

        recordListView = findViewById(R.id.record_list);
        if (recordListView.getAdapter() instanceof HeaderViewListAdapter) {
            HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) recordListView.getAdapter();
            recordItemAdapter = (RecordItemAdapter) headerViewListAdapter.getWrappedAdapter();
            fixListViewHeight(this.recordListView, contactRecordList.size());
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