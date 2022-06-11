package com.socialnet.android.gson;

import java.util.List;

public class FriendInfo {
    private List<FriendItem> friendItemList;

    public List<FriendItem> getFriendItemList() {
        return friendItemList;
    }

    public void setFriendItemList(List<FriendItem> friendItemList) {
        this.friendItemList = friendItemList;
    }
}
