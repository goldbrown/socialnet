package com.socialnet.android.db;

import org.litepal.crud.DataSupport;

public class ContactRecordEntity extends DataSupport {
    private String dateStr;
    private String friendName;

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }
}
