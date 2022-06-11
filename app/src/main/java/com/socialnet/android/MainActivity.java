package com.socialnet.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.socialnet.android.gson.ContactInfo;
import com.socialnet.android.gson.ContactRecord;
import com.socialnet.android.gson.FriendInfo;
import com.socialnet.android.gson.FriendItem;
import com.socialnet.android.gson.OneSentenceResponse;
import com.socialnet.android.util.HttpUtil;
import com.socialnet.android.util.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private TextView fromPerson;
    private TextView toPerson;

    private LinearLayout friendArea;

    private TextView oneSentenceText;
    private TextView friendName;
    private TextView friendTag;
    private TextView contactCount;

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
        socialNetLayout = findViewById(R.id.socialnet_layout);
        pageTitle = findViewById(R.id.page_title);

        // 记录表
        recordArea =findViewById(R.id.record_area);
        dateText = findViewById(R.id.date_text);
        fromPerson = findViewById(R.id.from_person);
        toPerson = findViewById(R.id.to_person);

        // 朋友表
        friendArea = findViewById(R.id.friend_area);
        friendName = findViewById(R.id.friend_name);
        friendTag = findViewById(R.id.friend_tag);
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
        ContactInfo contactInfo = getContactInfoFromDb(10);
        if (contactInfo != null) {
            for (ContactRecord record : contactInfo.getContactRecords()) {
                View view = LayoutInflater.from(this).inflate(R.layout.record_item, recordArea, false);
                TextView dateText =  view.findViewById(R.id.date_text);
                TextView fromPerson = view.findViewById(R.id.from_person);
                TextView toPerson = view.findViewById(R.id.to_person);
                dateText.setText(record.getDate());
                fromPerson.setText(record.getFromPerson());
                toPerson.setText(record.getToPerson());
                recordArea.addView(view);
            }
        }
        FriendInfo friendInfo = statFriendInfo(contactInfo);
        if (friendInfo != null) {
            for (FriendItem record : friendInfo.getFriendItemList()) {
                View view = LayoutInflater.from(this).inflate(R.layout.friend_item, friendArea, false);
                TextView friendName = view.findViewById(R.id.friend_name);
                TextView count = view.findViewById(R.id.contact_count);
                TextView tag = view.findViewById(R.id.friend_tag);
                friendName.setText(record.getFriendName());
                count.setText(String.format("%d", record.getCount()));
                tag.setText(record.getFriendTag());
                friendArea.addView(view);
            }
        }
    }

    private FriendInfo statFriendInfo(ContactInfo contactInfo) {
        FriendInfo friendInfo = new FriendInfo();
        List<FriendItem> itemList = new ArrayList<>();

        itemList.add(new FriendItem.Builder().friendName("张三").count(13).friendTag("亲人").build());
        itemList.add(new FriendItem.Builder().friendName("李四").count(1).friendTag("朋友").build());
        friendInfo.setFriendItemList(itemList);
        return friendInfo;
    }

    private ContactInfo getContactInfoFromDb(int limit) {
        ContactInfo contactInfo = new ContactInfo();
        List<ContactRecord> records = new ArrayList<>();

        records.add(new ContactRecord.Builder().fromPerson("zhangsan").toPerson("lisi")
                .date("2022-06-10 12:12:22").build());
        records.add(new ContactRecord.Builder().fromPerson("lisi").toPerson("zhangsan")
                .date("2022-06-11 12:12:22").build());
        records.add(new ContactRecord.Builder().fromPerson("zhangsan").toPerson("wangwu")
                .date("2022-06-11 12:12:22").build());
        contactInfo.setContactRecords(records);
        return contactInfo;
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