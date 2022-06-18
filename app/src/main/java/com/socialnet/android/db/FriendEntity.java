package com.socialnet.android.db;

import org.litepal.crud.DataSupport;

public class FriendEntity extends DataSupport {
    private String friendName;
    private String addTime;

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }
}
